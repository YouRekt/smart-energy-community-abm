package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.metering.Panic;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Date;

import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.metering.CalculateEnergyBalanceBehaviour.SHORTFALL;

public final class PrepareAndSendPostponeCFPBehaviour extends OneShotBehaviour {
    public static final String CFP_REPLY_BY = "cfp-reply-by";
    private static final long REPLY_BY_DELAY = 300;
    private final CommunityCoordinatorAgent agent;

    public PrepareAndSendPostponeCFPBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void action() {
        Double shortfall = (Double) getDataStore().get(SHORTFALL);
        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
        agent.householdAgents.forEach(cfp::addReceiver);
        cfp.setContent(String.valueOf(shortfall));
        Date replyBy = new Date(System.currentTimeMillis() + REPLY_BY_DELAY);
        cfp.setReplyByDate(replyBy);
        agent.send(cfp);

        getDataStore().put(CFP_REPLY_BY, replyBy);
        agent.log("Sent postpone CFP to households", LogSeverity.DEBUG, agent);
    }
}