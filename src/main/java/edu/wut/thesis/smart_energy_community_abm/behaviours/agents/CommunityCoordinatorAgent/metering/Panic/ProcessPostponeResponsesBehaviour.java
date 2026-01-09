package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.metering.Panic;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Date;
import java.util.Map;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Metering.SHORTFALL;

public final class ProcessPostponeResponsesBehaviour extends OneShotBehaviour {
    private static final int REPLY_BY_DELAY = 300;

    private final CommunityCoordinatorAgent agent;

    public ProcessPostponeResponsesBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void action() {
        Map<AID, Double> responses = (Map<AID, Double>) getDataStore().get(DataStoreKey.Metering.Panic.POSTPONE_RESPONSES);
        Double shortfall = (Double) getDataStore().get(SHORTFALL);

        Double freedEnergy = 0.0;

        // TODO: Choose households based on priority

        ACLMessage acceptedProposals = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
        ACLMessage rejectedProposals = new ACLMessage(ACLMessage.REJECT_PROPOSAL);

        boolean isShortfallSaturated = false;

        if (responses != null) {
            for (var entry : responses.entrySet()) {
                if (isShortfallSaturated) {
                    rejectedProposals.addReceiver(entry.getKey());
                    continue;
                }

                freedEnergy += entry.getValue();
                acceptedProposals.addReceiver(entry.getKey());

                if (shortfall <= freedEnergy)
                    isShortfallSaturated = true;
            }
        }

        double remainingShortfall = Math.max(0, shortfall - freedEnergy);

        if (remainingShortfall > 0) {
            agent.log("Remaining shortfall after postponements: " + remainingShortfall +
                    " (will drain battery, excess will be pulled from external grid)", LogSeverity.INFO, this);
        } else {
            agent.log("Panic will be resolved after postponements for tick " + agent.tick +
                    ", sending confirmations to HouseholdCoordinators", LogSeverity.DEBUG, this);
        }

        Date replyBy = new Date(System.currentTimeMillis() + REPLY_BY_DELAY);
        acceptedProposals.setReplyByDate(replyBy);
        rejectedProposals.setReplyByDate(replyBy);

        getDataStore().put(DataStoreKey.Metering.Panic.ACCEPT_PROPOSAL_REPLY_BY, replyBy);

        agent.send(acceptedProposals);
        agent.send(rejectedProposals);
    }
}