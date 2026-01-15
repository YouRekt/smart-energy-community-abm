package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.metering.Panic;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.TimeoutMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.*;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Metering.Panic.POSTPONE_RESPONSES;

public final class CollectPostponeResponsesBehaviour extends TimeoutMessageHandlerBehaviour {

    private final CommunityCoordinatorAgent agent;

    private final Map<AID, Double> responses = new HashMap<>();

    public CollectPostponeResponsesBehaviour(CommunityCoordinatorAgent agent) {
        super(agent, DataStoreKey.Metering.Panic.CFP_REPLY_BY);
        this.agent = agent;
    }

    @Override
    public void onStart() {
        super.onStart();
        responses.clear();
        setExpectedResponses(agent.householdAgents.size());
    }

    @Override
    public int onEnd() {
        getDataStore().put(POSTPONE_RESPONSES, responses);
        return super.onEnd();
    }

    @Override
    protected void handlePropose(ACLMessage msg) {
        if (!isMessageTimely(msg)) {
            agent.log("Received a stale message " + ((msg.getContent() == null) ? "" : msg.getContent()), LogSeverity.WARN, this);
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
            agent.log("Received a stale message " + ((msg.getContent() == null) ? "" : msg.getContent()), LogSeverity.WARN, this);
        } else {
            agent.log("Household refused postponement", LogSeverity.DEBUG, this);
            incrementReceivedCount();
        }
    }
}