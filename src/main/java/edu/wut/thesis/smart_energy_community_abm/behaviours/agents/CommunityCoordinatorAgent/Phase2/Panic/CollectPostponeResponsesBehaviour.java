package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2.Panic;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.PostponeResponse;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class CollectPostponeResponsesBehaviour extends BaseMessageHandlerBehaviour {
    public static final String POSTPONE_RESPONSES = "postpone-responses";
    private final List<PostponeResponse> responses = new ArrayList<>();
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
    protected void handleAcceptProposal(ACLMessage msg) {
        double energyFreed = Double.parseDouble(msg.getContent());
        responses.add(new PostponeResponse(msg.getSender(), true, energyFreed));
    }

    @Override
    protected void handleRejectProposal(ACLMessage msg) {
        responses.add(new PostponeResponse(msg.getSender(), false, 0.0));
    }

    @Override
    public int onEnd() {
        getDataStore().put(POSTPONE_RESPONSES, new ArrayList<>(responses));
        return super.onEnd();
    }

    @Override
    public boolean done() {
        if (expectedResponses == 0) return true;
        if (responses.size() >= expectedResponses) return true;

        Date replyBy = (Date) getDataStore().get(PrepareAndSendPostponeCFPBehaviour.CFP_REPLY_BY);
        return replyBy != null && replyBy.before(new Date());
    }
}