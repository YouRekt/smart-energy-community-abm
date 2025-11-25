package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.LogSeverity;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Date;

public final class RequestEnergyStatusBehaviour extends OneShotBehaviour {
    public static final String REQUEST_REPLY_BY = "request-reply-by";
    private static final long REPLY_BY_DELAY = 400;
    private final CommunityCoordinatorAgent agent;

    public RequestEnergyStatusBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void action() {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        agent.energyAgents.forEach(msg::addReceiver);
        msg.addReceiver(agent.batteryAgent);
        Date replyBy = new Date(System.currentTimeMillis() + REPLY_BY_DELAY);
        msg.setReplyByDate(replyBy);
        agent.send(msg);
        getDataStore().put(REQUEST_REPLY_BY, replyBy);
    }
}
