package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.OneShotBehaviour;

import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2.CollectEnergyStatusBehaviour.CURRENT_CHARGE;
import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2.CollectEnergyStatusBehaviour.POWER_PRODUCED;

public final class CalculateEnergyBalanceBehaviour extends OneShotBehaviour {
    public static final String AVAILABLE_ENERGY = "available-energy";
    static final int NO_PANIC = 0;
    static final int HAS_PANIC = 1;
    private final CommunityCoordinatorAgent agent;
    private Double availableEnergy;

    public CalculateEnergyBalanceBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void action() {
        Double currentCharge = (Double) getDataStore().get(CURRENT_CHARGE);
        Double powerProduced = (Double) getDataStore().get(POWER_PRODUCED);
        availableEnergy = currentCharge + powerProduced;

        agent.log("Available energy: " + availableEnergy, LogSeverity.DEBUG, this);

        getDataStore().put(AVAILABLE_ENERGY, availableEnergy);
    }

    @Override
    public int onEnd() {
        return availableEnergy > agent.getAllocatedAt(agent.tick) ? NO_PANIC : HAS_PANIC;
    }
}
