package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase2.Panic;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.AllocationEntry;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Date;

import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase2.CollectEnergyStatusBehaviour.PANIC_CFP;

public class SendPostponeRequestsBehaviour extends OneShotBehaviour {
    public static final String POSTPONE_REPLY_BY = "postpone-reply-by";
    public static final String POSTPONE_REPLY_COUNT = "postpone-reply-count";
    private static final long REPLY_BY_DELAY = 500;
    private final HouseholdCoordinatorAgent agent;

    public SendPostponeRequestsBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;

    }

    @Override
    public void action() {
        ACLMessage cfp = (ACLMessage) getDataStore().get(PANIC_CFP);

        long tickToPostpone = Long.parseLong(cfp.getContent());
        agent.log("Received postpone CFP for tick " + tickToPostpone, LogSeverity.DEBUG, agent);

        ACLMessage postponeRequest = new ACLMessage(ACLMessage.REQUEST);
        postponeRequest.setContent(String.valueOf(tickToPostpone));

        int replyCount = 0;
        for (AllocationEntry allocationEntry : agent.timetable.get(tickToPostpone)) {
            postponeRequest.addReceiver(allocationEntry.requesterId());
            replyCount++;
        }

        getDataStore().put(replyCount, POSTPONE_REPLY_COUNT);

        Date replyBy = new Date(System.currentTimeMillis() + REPLY_BY_DELAY);
        postponeRequest.setReplyByDate(replyBy);

        agent.send(postponeRequest);
        getDataStore().put(POSTPONE_REPLY_BY, replyBy);
    }
}
