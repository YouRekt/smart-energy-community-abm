package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.discovery;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey;
import edu.wut.thesis.smart_energy_community_abm.domain.messages.TopicHelper;
import jade.core.AID;
import jade.core.ServiceException;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Date;

import static edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent.REPLY_BY_DELAY;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Discovery.HEALTH_REPLY_BY;

public final class ApplianceTickRelayBehaviour extends OneShotBehaviour {
    private final AID topic;
    private final HouseholdCoordinatorAgent agent;

    public ApplianceTickRelayBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;

        try {
            this.topic = TopicHelper.getTopic(this.agent, this.agent.getLocalName());
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void action() {
        agent.healthyAppliances.clear();

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(topic);
        msg.setContent(Long.toString(agent.tick));

        Date replyBy = new Date(System.currentTimeMillis() + (long) (REPLY_BY_DELAY * 0.7));

        msg.setReplyByDate(replyBy);
        getDataStore().put(HEALTH_REPLY_BY, replyBy);

        agent.send(msg);
    }
}
