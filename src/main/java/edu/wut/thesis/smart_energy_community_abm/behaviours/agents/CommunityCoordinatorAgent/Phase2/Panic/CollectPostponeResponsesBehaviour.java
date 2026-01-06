package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2.Panic;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.*;

public final class CollectPostponeResponsesBehaviour extends BaseMessageHandlerBehaviour {
    public static final String POSTPONE_RESPONSES = "postpone-responses";
    private final Map<AID, Double> responses = new HashMap<>();
    private int expectedResponses = 0;

    public CollectPostponeResponsesBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onStart() {
        responses.clear();
        List<AID> targets = (List<AID>) getDataStore().get(PrepareAndSendPostponeCFPBehaviour.CFP_TARGETS);
        expectedResponses = (targets != null) ? targets.size() : 0;
    }

    @Override
    protected void handlePropose(ACLMessage msg) {

        responses.put(msg.getSender(), Double.parseDouble(msg.getContent()));
    }

    @Override
    protected void handleRefuse(ACLMessage msg) {
        // TODO: Check if we need to do anything when HouseCoordinator refuses
        agent.log("Household refused", LogSeverity.DEBUG, this);
    }

    @Override
    public int onEnd() {
        getDataStore().put(POSTPONE_RESPONSES, responses);
        return super.onEnd();
    }

    @Override
    public boolean done() {
        if (expectedResponses == 0) return true;
        if (responses.size() >= expectedResponses) return true;

        Date replyBy = (Date) getDataStore().get(PrepareAndSendPostponeCFPBehaviour.CFP_REPLY_BY);
        return replyBy != null && replyBy.before(new Date());
    }


    @Override
    protected void performBlock() {
        Date replyBy = (Date) getDataStore().get(PrepareAndSendPostponeCFPBehaviour.CFP_REPLY_BY);

        block(replyBy.getTime() - System.currentTimeMillis());
    }
}