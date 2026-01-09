package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.metering;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Date;

public final class RequestEnergyUsageBehaviour extends OneShotBehaviour {
    private static final long REPLY_BY_DELAY = 400;

    private final CommunityCoordinatorAgent agent;

    public RequestEnergyUsageBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void action() {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        agent.householdAgents.forEach(msg::addReceiver);
        Date replyBy = new Date(System.currentTimeMillis() + REPLY_BY_DELAY);
        msg.setReplyByDate(replyBy);
        agent.send(msg);
        getDataStore().put(DataStoreKey.Metering.REQUEST_REPLY_BY, replyBy);
    }
}
