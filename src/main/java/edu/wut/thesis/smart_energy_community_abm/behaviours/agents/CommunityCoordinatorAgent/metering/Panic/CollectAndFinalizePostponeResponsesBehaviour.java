package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.metering.Panic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.TimeoutMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Metering.Panic.ACCEPT_PROPOSAL_MSG_COUNT;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Metering.Panic.ACCEPT_PROPOSAL_REPLY_BY;

public final class CollectAndFinalizePostponeResponsesBehaviour extends TimeoutMessageHandlerBehaviour {
    private final CommunityCoordinatorAgent agent;
    private final TreeMap<Long, Map<AID, Double>> allocationsToClear = new TreeMap<>();

    public CollectAndFinalizePostponeResponsesBehaviour(CommunityCoordinatorAgent agent) {
        super(agent, ACCEPT_PROPOSAL_REPLY_BY);
        this.agent = agent;
    }

    @Override
    public void onStart() {
        super.onStart();
        allocationsToClear.clear();

        Integer expected = (Integer) getDataStore().get(ACCEPT_PROPOSAL_MSG_COUNT);
        if (expected != null) {
            setExpectedResponses(expected);
        } else {
            agent.log("Expected replies count is null", LogSeverity.ERROR, this);
        }
    }

    @Override
    public int onEnd() {
        allocationsToClear.forEach((currentTick, allocationReductionMap) -> {
            Map<AID, Double> targetInnerMap = agent.allocations.get(currentTick);

            if (targetInnerMap != null) {
                allocationReductionMap.forEach((aid, reductionAmount) ->
                        targetInnerMap.computeIfPresent(aid, (_, existingEntry) -> existingEntry - reductionAmount));
            }
        });

        return super.onEnd();
    }

    @Override
    protected void handleInform(ACLMessage msg) {
        if (!isMessageTimely(msg)) {
            agent.log("Received a stale message " + ((msg.getContent() == null) ? "" : msg.getContent()), LogSeverity.WARN, this);
        } else {
            incrementReceivedCount();
            try {
                ObjectMapper mapper = new ObjectMapper();
                Map<Long, Double> energyFreed = mapper.readValue(
                        msg.getContent(),
                        new TypeReference<>() {
                        }
                );

                for (var entry : energyFreed.entrySet()) {
                    Long currentTick = entry.getKey();
                    Double currentSum = entry.getValue();

                    allocationsToClear.computeIfAbsent(currentTick, _ -> new HashMap<>()).put(msg.getSender(), currentSum);
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
