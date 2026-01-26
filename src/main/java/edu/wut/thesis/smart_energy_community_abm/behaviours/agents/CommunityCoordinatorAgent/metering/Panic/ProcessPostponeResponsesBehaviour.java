package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.metering.Panic;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.*;

import static edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent.REPLY_BY_DELAY;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Metering.Panic.ACCEPT_PROPOSAL_MSG_COUNT;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Metering.Panic.ACCEPT_PROPOSAL_REPLY_BY;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Metering.SHORTFALL;

public final class ProcessPostponeResponsesBehaviour extends OneShotBehaviour {
    private final CommunityCoordinatorAgent agent;

    public ProcessPostponeResponsesBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void action() {
        Map<AID, Double> responses = (Map<AID, Double>) getDataStore().get(DataStoreKey.Metering.Panic.POSTPONE_RESPONSES);
        Double shortfallRaw = (Double) getDataStore().get(SHORTFALL);
        double shortfall = (shortfallRaw != null) ? shortfallRaw : 0.0;

        double freedEnergy = 0.0;
        double currentShortfall = shortfall;

        ACLMessage acceptedProposals = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
        ACLMessage rejectedProposals = new ACLMessage(ACLMessage.REJECT_PROPOSAL);

        int acceptedProposalsCount = 0;

        if (responses != null && !responses.isEmpty()) {
            List<Map.Entry<AID, Double>> candidates = new ArrayList<>(responses.entrySet());

            candidates.sort(Comparator.comparingDouble(entry ->
                    agent.computePostponementPriority(entry.getKey(), entry.getValue())
            ));

            for (Map.Entry<AID, Double> entry : candidates) {
                AID household = entry.getKey();
                Double energyFromProposal = entry.getValue();

                double greenScore = agent.greenScores.getOrDefault(household, 0.0);
                double coopScore = agent.cooperationScores.getOrDefault(household, 0.5);
                double avgProduction = agent.runningAvgProduction;

                double gridAllowance = agent.strategy.getPanicGridAllowance(greenScore, coopScore, avgProduction);

                if (currentShortfall > gridAllowance) {
                    freedEnergy += energyFromProposal;
                    currentShortfall -= energyFromProposal;
                    acceptedProposals.addReceiver(household);
                    acceptedProposalsCount++;

                    agent.rewardPostponement(household);

                    agent.log("Postponing agent " + household.getLocalName() +
                            " (Energy: " + energyFromProposal + ", Allowance: " + String.format("%.2f", gridAllowance) +
                            ") to address shortfall.", LogSeverity.DEBUG, this);
                } else {
                    rejectedProposals.addReceiver(household);
                }
            }
        }

        double finalRemainingShortfall = Math.max(0, shortfall - freedEnergy);

        if (finalRemainingShortfall > 0) {
            agent.log("Remaining shortfall after postponements: " + finalRemainingShortfall +
                    " (will drain battery, excess will be pulled from external grid)", LogSeverity.INFO, this);
        } else {
            agent.log("Panic resolved (or mitigated) after postponements for tick " + agent.tick +
                    ", sending confirmations to HouseholdCoordinators", LogSeverity.INFO, this);
        }

        Date replyBy = new Date(System.currentTimeMillis() + REPLY_BY_DELAY);
        acceptedProposals.setReplyByDate(replyBy);
        rejectedProposals.setReplyByDate(replyBy);

        getDataStore().put(ACCEPT_PROPOSAL_REPLY_BY, replyBy);
        getDataStore().put(ACCEPT_PROPOSAL_MSG_COUNT, acceptedProposalsCount);

        agent.send(acceptedProposals);
        agent.send(rejectedProposals);
    }
}