package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.metering.Panic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class CollectAndFinalizePostponeResponsesBehaviour extends BaseMessageHandlerBehaviour {
    private final CommunityCoordinatorAgent agent;
    private final TreeMap<Long, Map<AID, Double>> allocationsToClear = new TreeMap<>();

    public CollectAndFinalizePostponeResponsesBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public boolean done() {
        Date replyBy = (Date) getDataStore().get(ProcessPostponeResponsesBehaviour.ACCEPT_PROPOSAL_REPLY_BY);
        return replyBy != null && replyBy.before(new Date());
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
    protected void performBlock() {
        Date replyBy = (Date) getDataStore().get(ProcessPostponeResponsesBehaviour.ACCEPT_PROPOSAL_REPLY_BY);

        block(replyBy.getTime() - System.currentTimeMillis());
    }

    @Override
    protected void handleInform(ACLMessage msg) {
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

    @Override
    public void onStart() {
        allocationsToClear.clear();
    }
}
