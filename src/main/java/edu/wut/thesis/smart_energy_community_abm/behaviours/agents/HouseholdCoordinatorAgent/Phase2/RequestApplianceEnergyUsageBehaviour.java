package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase2;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Date;

public final class RequestApplianceEnergyUsageBehaviour extends OneShotBehaviour {
    public static final String REQUEST_REPLY_BY = "request-reply-by";
    private static final long REPLY_BY_DELAY = 200;
    private final HouseholdCoordinatorAgent agent;

    public RequestApplianceEnergyUsageBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void action() {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        agent.healthyAppliances.forEach(msg::addReceiver);
        Date replyBy = new Date(System.currentTimeMillis() + REPLY_BY_DELAY);
        msg.setReplyByDate(replyBy);
        agent.send(msg);
        getDataStore().put(REQUEST_REPLY_BY, replyBy);
    }
}
