package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.negotiation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.ApplianceTask;
import edu.wut.thesis.smart_energy_community_abm.domain.EnergyRequest;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Negotiation.ALLOCATION_REQUEST_MSG;

public final class SendAllocationRequestBehaviour extends OneShotBehaviour {
    private final ApplianceAgent agent;
    private boolean hasMadeRequest = false;

    public SendAllocationRequestBehaviour(ApplianceAgent agent) {
        this.agent = agent;
    }


    @Override
    public void action() {
        final ACLMessage requestMsg = ((ACLMessage) getDataStore().get(ALLOCATION_REQUEST_MSG));
        if (requestMsg == null) {
            agent.log("Allocation request message from Household Coordinator is null/missing", LogSeverity.ERROR, this);
            return;
        }

        ACLMessage reply = requestMsg.createReply();

        List<EnergyRequest> requests = new ArrayList<>();

        // Iterate over all tasks this appliance is responsible for
        for (ApplianceTask task : agent.taskSchedule.keySet()) {
            if (agent.shouldTaskRun(task, agent.tick)) {
                // Task wants to run. Find a suitable slot.
                long proposedStartTick = agent.findFirstAvailableSlot(task);

                if (proposedStartTick != -1) {
                    requests.add(new EnergyRequest(
                            agent.getAID(),
                            proposedStartTick,
                            task.duration(),
                            task.energyPerTick(),
                            task.taskId()
                    ));
                } else {
                    agent.log("Could not find a slot for task: " + task.taskName() + " (duration: " + task.duration() + ")", LogSeverity.WARN, this);
                }
            }
        }

        if (requests.isEmpty()) {
            reply.setPerformative(ACLMessage.REFUSE);
            reply.setContent("No tasks to schedule");
            agent.send(reply);
            agent.log("Sent REFUSE (No tasks needed)", LogSeverity.DEBUG, this);
            hasMadeRequest = false;
        } else {
            reply.setPerformative(ACLMessage.INFORM);
            try {
                ObjectMapper mapper = new ObjectMapper();
                reply.setContent(mapper.writeValueAsString(requests));
                agent.send(reply);
                agent.log("Sent INFORM with " + requests.size() + " allocation requests", LogSeverity.INFO, this);
                hasMadeRequest = true;
                agent.pushTaskRequested();
            } catch (JsonProcessingException e) {
                agent.log("Failed to serialize EnergyRequests", LogSeverity.ERROR, this);
            }
        }
    }

    @Override
    public int onEnd() {
        return hasMadeRequest ? ACLMessage.INFORM : ACLMessage.REFUSE;
    }
}
