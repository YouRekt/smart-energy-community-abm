package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.negotiation;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Date;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Negotiation.REQUEST_AMOUNT;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Negotiation.REQUEST_REPLY_BY;

public final class SendInitialRequestsToHouseholdsBehaviour extends OneShotBehaviour {
    private final static long REPLY_BY_DELAY = 100;
    private final CommunityCoordinatorAgent agent;

    public SendInitialRequestsToHouseholdsBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void action() {
        final ACLMessage request = new ACLMessage(ACLMessage.REQUEST);

        agent.householdAgents.forEach(request::addReceiver);
        Date replyBy = new Date(System.currentTimeMillis() + REPLY_BY_DELAY);
        request.setReplyByDate(replyBy);
        agent.send(request);

        getDataStore().put(REQUEST_REPLY_BY, replyBy);
        getDataStore().put(REQUEST_AMOUNT, agent.householdAgents.size());
    }
}
