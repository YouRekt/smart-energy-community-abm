package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase1;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.LogSeverity;
import edu.wut.thesis.smart_energy_community_abm.domain.MessageSubject;
import edu.wut.thesis.smart_energy_community_abm.domain.TopicHelper;
import jade.core.AID;
import jade.core.ServiceException;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Date;

public final class StartNewTickBehaviour extends OneShotBehaviour {
    public static final String TICK_REPLY_BY = "tick-reply-by";
    private static final long REPLY_BY_DELAY = 500;
    private final AID topic;
    private final CommunityCoordinatorAgent agent;

    public StartNewTickBehaviour(CommunityCoordinatorAgent agent) {
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
        agent.healthyAgents.clear();
        agent.tick++;

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(topic);
        msg.setContent(Long.toString(agent.tick));
        Date replyBy = new Date(System.currentTimeMillis() + REPLY_BY_DELAY);
        msg.setReplyByDate(replyBy);
        agent.send(msg);

        getDataStore().put(TICK_REPLY_BY, replyBy);
        agent.log(String.format("--- Starting Tick %d ---", agent.tick), LogSeverity.INFO);
    }
}
