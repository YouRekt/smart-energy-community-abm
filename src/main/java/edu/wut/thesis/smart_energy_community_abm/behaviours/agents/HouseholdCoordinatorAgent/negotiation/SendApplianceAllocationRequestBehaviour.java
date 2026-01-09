package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.negotiation;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Date;

public class SendApplianceAllocationRequestBehaviour extends OneShotBehaviour {
    public static final String REQUEST_REPLY_BY = "request-reply-by";
    private static final long REPLY_BY_DELAY = 500;
    public static final String REQUEST_REPLY_COUNT = "request-reply-count";
    private final HouseholdCoordinatorAgent agent;

    public SendApplianceAllocationRequestBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void action() {
        final ACLMessage request = new ACLMessage(ACLMessage.REQUEST);

        request.setContent("Send over task requests");

        int replyCount = 0;
        for (AID aid : agent.healthyAppliances) {
            request.addReceiver(aid);
            replyCount++;
        }

        getDataStore().put(replyCount, REQUEST_REPLY_COUNT);

        Date replyBy = new Date(System.currentTimeMillis() + REPLY_BY_DELAY);
        request.setReplyByDate(replyBy);

        agent.send(request);
        getDataStore().put(REQUEST_REPLY_BY, replyBy);
    }
}
