package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.discovery;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.messages.MessageSubject;
import edu.wut.thesis.smart_energy_community_abm.domain.messages.TopicHelper;
import jade.core.AID;
import jade.core.ServiceException;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Date;

import static edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent.REPLY_BY_DELAY;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Discovery.TICK_REPLY_BY;

public final class SendNewTickBehaviour extends OneShotBehaviour {
    private final AID topic;
    private final CommunityCoordinatorAgent agent;

    public SendNewTickBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;

        try {
            this.topic = TopicHelper.getTopic(agent, MessageSubject.TICK);
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void action() {
        // Reset state
        agent.energyAgents.clear();
        agent.householdAgents.clear();
        agent.batteryAgent = null;

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(topic);
        msg.setContent(Long.toString(agent.tick));
        Date replyBy = new Date(System.currentTimeMillis() + REPLY_BY_DELAY);
        msg.setReplyByDate(replyBy);
        agent.send(msg);

        getDataStore().put(TICK_REPLY_BY, replyBy);
    }
}
