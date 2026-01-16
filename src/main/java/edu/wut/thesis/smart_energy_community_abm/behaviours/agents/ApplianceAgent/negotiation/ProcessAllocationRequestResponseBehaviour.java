package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.negotiation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.ApplianceTask;
import edu.wut.thesis.smart_energy_community_abm.domain.ApplianceTaskInstance;
import edu.wut.thesis.smart_energy_community_abm.domain.EnergyRequest;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.List;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Negotiation.ALLOCATION_RESPONSE_MSG;

public final class ProcessAllocationRequestResponseBehaviour extends OneShotBehaviour {
    private final ApplianceAgent agent;

    public ProcessAllocationRequestResponseBehaviour(ApplianceAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void action() {
        final ACLMessage householdReply = (ACLMessage) getDataStore().get(ALLOCATION_RESPONSE_MSG);

        if (householdReply == null) {
            agent.log("Allocation response message is null", LogSeverity.ERROR, this);
            return;
        }

        switch (householdReply.getPerformative()) {
            case ACLMessage.CONFIRM -> {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    List<EnergyRequest> requests = mapper.readValue(householdReply.getContent(), new TypeReference<>() {
                    });

                    for (EnergyRequest req : requests) {
                        ApplianceTask task = agent.taskSchedule.keySet().stream()
                                .filter(t -> t.taskId() == req.taskId())
                                .findFirst()
                                .orElse(null);

                        if (task != null) {
                            ApplianceTaskInstance instance = new ApplianceTaskInstance(task, req.startTick(), task.duration());

                            long endTick = req.startTick() + req.duration();
                            for (long t = req.startTick(); t < endTick; t++) {
                                agent.timetable.put(t, instance);
                            }

                            agent.log("Scheduled task " + task.taskName() + " (ID: " + task.taskId() + ") starting at tick " + req.startTick(), LogSeverity.INFO, this);

                            final ACLMessage acknowledgeReply = householdReply.createReply(ACLMessage.INFORM);
                            agent.send(acknowledgeReply);
                        } else {
                            agent.log("Received confirmation for unknown taskId: " + req.taskId(), LogSeverity.WARN, this);
                        }
                    }
                } catch (JsonProcessingException e) {
                    agent.log("Failed to parse allocation response JSON", LogSeverity.ERROR, this);
                    throw new RuntimeException(e);
                }
            }
            case ACLMessage.REFUSE -> {
                agent.log("Household Coordinator refused allocation requests - no tasks scheduled.", LogSeverity.INFO, this);
            }
        }
    }
}
