package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.metering.Panic;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Date;

import static edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent.REPLY_BY_DELAY;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Metering.PANIC_CFP;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Metering.Panic.POSTPONE_REPLY_BY;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Metering.Panic.POSTPONE_REPLY_COUNT;

public final class SendPostponeRequestsBehaviour extends OneShotBehaviour {
    private final HouseholdCoordinatorAgent agent;

    public SendPostponeRequestsBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;

    }

    @Override
    public void action() {
        ACLMessage cfp = (ACLMessage) getDataStore().get(PANIC_CFP);

        double shortfall = Double.parseDouble(cfp.getContent());
        agent.log("Received postpone CFP. Shortfall = " + shortfall, LogSeverity.DEBUG, agent);

        ACLMessage postponeRequest = new ACLMessage(ACLMessage.CFP);
        postponeRequest.setContent(String.valueOf(shortfall));

        int replyCount = 0;
        for (AID aid : agent.getActiveAppliancesAt(agent.tick)) {
            postponeRequest.addReceiver(aid);
            replyCount++;
        }

        getDataStore().put(POSTPONE_REPLY_COUNT, replyCount);

        Date replyBy = new Date(System.currentTimeMillis() + (long) (REPLY_BY_DELAY * 0.7));
        postponeRequest.setReplyByDate(replyBy);

        agent.send(postponeRequest);
        getDataStore().put(POSTPONE_REPLY_BY, replyBy);
    }
}
