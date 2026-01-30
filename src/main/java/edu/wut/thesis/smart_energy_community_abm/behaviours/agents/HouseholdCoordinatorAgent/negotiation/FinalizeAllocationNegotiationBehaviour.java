package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.negotiation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.AllocationEntry;
import edu.wut.thesis.smart_energy_community_abm.domain.EnergyRequest;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent.REPLY_BY_DELAY;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Negotiation.*;

public final class FinalizeAllocationNegotiationBehaviour extends OneShotBehaviour {
    private final HouseholdCoordinatorAgent agent;

    public FinalizeAllocationNegotiationBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void action() {
        try {
            final Map<AID, List<EnergyRequest>> requestedAllocations = (Map<AID, List<EnergyRequest>>) getDataStore().get(REQUESTED_ALLOCATIONS);

            requestedAllocations.forEach((aid, list) -> list.forEach((e) -> {
                for (long t = e.startTick(); t <= e.endTick(); t++) {
                    agent.timetable.computeIfAbsent(t, _ -> new HashMap<>()).put(aid, new AllocationEntry(
                            e.energyPerTick(),
                            e.startTick(),
                            e.duration()
                    ));
                }
            }));

            final ObjectMapper mapper = new ObjectMapper();
            Date replyBy = new Date(System.currentTimeMillis() + (long) (REPLY_BY_DELAY * 0.7));
            getDataStore().put(CONFIRM_REPLY_BY_DELAY, replyBy);

            for (var entry : requestedAllocations.entrySet()) {
                final ACLMessage applianceReply = new ACLMessage(ACLMessage.CONFIRM);
                applianceReply.addReceiver(entry.getKey());
                applianceReply.setReplyByDate(replyBy);
                applianceReply.setContent(mapper.writeValueAsString(entry.getValue()));
                agent.send(applianceReply);
            }

            getDataStore().put(APPLIANCE_CONFIRM_MESSAGES, requestedAllocations.size());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
