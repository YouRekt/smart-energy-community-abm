package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.LogSeverity;
import jade.core.behaviours.SimpleBehaviour;

import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2.CollectEnergyStatusBehaviour.CURRENT_CHARGE;
import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2.CollectEnergyStatusBehaviour.POWER_PRODUCED;

public final class CalculateEnergyBalanceBehaviour extends SimpleBehaviour {
    public static final String AVAILABLE_ENERGY = "available-energy";
    private final CommunityCoordinatorAgent agent;

    public CalculateEnergyBalanceBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void action() {
        Double currentCharge = (Double) getDataStore().get(CURRENT_CHARGE);
        Double powerProduced = (Double) getDataStore().get(POWER_PRODUCED);
        Double availableEnergy = currentCharge + powerProduced;

        agent.log("Available energy: " + availableEnergy, LogSeverity.DEBUG, this);
        // TODO: Implement schedule object to read currently scheduled tasks for this tick and sum their power usages
        // agent.energyPanic = true;
        getDataStore().put(AVAILABLE_ENERGY, availableEnergy);
    }

    @Override
    public boolean done() {
        return true;
    }
}
