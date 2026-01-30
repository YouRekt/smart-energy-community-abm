package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.metering;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Date;

import static edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent.REPLY_BY_DELAY;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Metering.REQUEST_REPLY_BY;

public final class RequestEnergyStatusBehaviour extends OneShotBehaviour {
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
        msg.setContent("Requesting energy status");
        agent.send(msg);
        getDataStore().put(REQUEST_REPLY_BY, replyBy);
    }
}
