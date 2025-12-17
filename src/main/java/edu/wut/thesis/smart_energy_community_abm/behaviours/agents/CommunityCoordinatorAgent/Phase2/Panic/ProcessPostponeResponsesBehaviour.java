package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2.Panic;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.PostponeResponse;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.OneShotBehaviour;

import java.util.List;

import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2.CalculateEnergyBalanceBehaviour.SHORTFALL;

public final class ProcessPostponeResponsesBehaviour extends OneShotBehaviour {
    public static final String REMAINING_SHORTFALL = "remaining-shortfall";
    private final CommunityCoordinatorAgent agent;

    public ProcessPostponeResponsesBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void action() {
        List<PostponeResponse> responses = (List<PostponeResponse>) getDataStore().get(CollectPostponeResponsesBehaviour.POSTPONE_RESPONSES);
        Double shortfall = (Double) getDataStore().get(SHORTFALL);

        if (shortfall == null)
            throw new RuntimeException();

        // TODO: Handle cleaning the timetable + calculate remaining shortfall for Phase 2a

        double freedEnergy = 0.0;

        if (responses != null) {
            for (PostponeResponse resp : responses) {
                agent.updateCooperationScore(resp.householdId(), resp.accepted());

                if (resp.accepted()) {
                    freedEnergy += resp.energyFreed();
                    agent.removeAllocation(agent.tick, resp.householdId());
                    agent.log("Household " + resp.householdId().getLocalName() +
                            " accepted postpone, freed " + resp.energyFreed(), LogSeverity.DEBUG, agent);
                }
            }
        }

        double remainingShortfall = Math.max(0, shortfall - freedEnergy);

        if (remainingShortfall > 0) {
            agent.log("Remaining shortfall after postponements: " + remainingShortfall +
                    " (will drain battery, excess goes to grid)", LogSeverity.INFO, agent);
        } else {
            agent.log("Panic resolved after postponements", LogSeverity.DEBUG, agent);
        }

        getDataStore().put(REMAINING_SHORTFALL, remainingShortfall);
    }
}