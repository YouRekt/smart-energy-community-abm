package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase3;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Date;

// TODO: Create a commmon behaviour so we do not have to repeat this code
public final class AskForRequestsBehaviour extends OneShotBehaviour {
    // TODO: Move these strings somewhere or develop a cleaner way of using them
    public static final String REQUEST_REPLY_BY = "request-reply-by";
    private static final long REPLY_BY_DELAY = 400;
    private final CommunityCoordinatorAgent agent;

    public AskForRequestsBehaviour(CommunityCoordinatorAgent agent) {
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
        getDataStore().put(REQUEST_REPLY_BY, replyBy);
    }
}
