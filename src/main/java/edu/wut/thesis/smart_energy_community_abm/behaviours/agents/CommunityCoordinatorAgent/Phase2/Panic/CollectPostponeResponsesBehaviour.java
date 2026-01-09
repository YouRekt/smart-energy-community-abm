package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2.Panic;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.TimeoutMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.*;

public final class CollectPostponeResponsesBehaviour extends TimeoutMessageHandlerBehaviour {
    public static final String POSTPONE_RESPONSES = "postpone-responses";

    private final Map<AID, Double> responses = new HashMap<>();

    public CollectPostponeResponsesBehaviour(CommunityCoordinatorAgent agent) {
        super(agent, PrepareAndSendPostponeCFPBehaviour.CFP_REPLY_BY);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onStart() {
        super.onStart();
        responses.clear();

        List<AID> targets = (List<AID>) getDataStore().get(PrepareAndSendPostponeCFPBehaviour.CFP_TARGETS);
        if (targets != null) {
            setExpectedResponses(targets.size());
        }
    }

    @Override
    public int onEnd() {
        getDataStore().put(POSTPONE_RESPONSES, responses);
        return super.onEnd();
    }

    @Override
    protected void handlePropose(ACLMessage msg) {
        if (!isMessageTimely(msg)) {
            agent.log("Received a stale message " + ((msg.getContent() == null) ? "" : msg.getContent()), LogSeverity.WARNING, this);
        } else {
            try {
                responses.put(msg.getSender(), Double.parseDouble(msg.getContent()));
                incrementReceivedCount();
            } catch (NumberFormatException e) {
                agent.log("Invalid energy amount in proposal", LogSeverity.ERROR, this);
            }
        }
    }

    @Override
    protected void handleRefuse(ACLMessage msg) {
        if (!isMessageTimely(msg)) {
            agent.log("Received a stale message " + ((msg.getContent() == null) ? "" : msg.getContent()), LogSeverity.WARNING, this);
        } else {
            agent.log("Household refused postponement", LogSeverity.DEBUG, this);
            incrementReceivedCount();
        }
    }
}