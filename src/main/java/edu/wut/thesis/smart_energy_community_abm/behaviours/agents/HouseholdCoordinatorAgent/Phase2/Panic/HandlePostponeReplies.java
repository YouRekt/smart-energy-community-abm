package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase2.Panic;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2.RequestEnergyStatusBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase2.Panic.SendPostponeRequestsBehaviour.POSTPONE_REPLY_COUNT;

public class HandlePostponeReplies extends BaseMessageHandlerBehaviour {
    public static final String POSTPONE_AGREEMENTS = "postpone-agreements";
    private int repliesReceived = 0;
    private final List<AID> postponeAgreements = new ArrayList<AID>();
    public HandlePostponeReplies(HouseholdCoordinatorAgent agent) {
        super(agent);
    }

    @Override
    protected void handleAgree(ACLMessage msg) {
        repliesReceived++;
        postponeAgreements.add(msg.getSender());
    }

    @Override
    protected void handleRefuse(ACLMessage msg) {
        repliesReceived++;
    }

    @Override
    public boolean done() {
        final Integer expectedReplies = (Integer) getDataStore().get(POSTPONE_REPLY_COUNT);
        if (expectedReplies == null ||  expectedReplies == 0)
            return true;

        Date replyBy = (Date) getDataStore().get(SendPostponeRequestsBehaviour.POSTPONE_REPLY_BY);
        return replyBy.before(new Date()) || repliesReceived == expectedReplies;
    }

    @Override
    public int onEnd() {
        getDataStore().put(POSTPONE_AGREEMENTS, postponeAgreements);
        return super.onEnd();
    }
}
