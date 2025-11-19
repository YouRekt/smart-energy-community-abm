package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.TopicHelper;
import edu.wut.thesis.smart_energy_community_abm.util.LogSeverity;
import jade.core.AID;
import jade.core.ServiceException;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Date;

public class StartNewTickBehaviour extends OneShotBehaviour {
    public final static String TICK_REPLY_BY = "tick-reply-by";
    private static final int REPLY_BY_DELAY = 100;
    private final AID topic;
    private final CommunityCoordinatorAgent agent;

    public StartNewTickBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;

        try {
            this.topic = TopicHelper.getTopic(agent, "TICK");
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void action() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(topic);
        msg.setContent(Long.toString(agent.tick));
        Date replyBy = new Date(System.currentTimeMillis() + REPLY_BY_DELAY);
        msg.setReplyByDate(replyBy);
        agent.send(msg);
        getDataStore().put(TICK_REPLY_BY, replyBy);
        agent.log(String.format("Tick %d", agent.tick), LogSeverity.INFO);
    }
}
