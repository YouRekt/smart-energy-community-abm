package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.metering;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.TransitionKeys;
import jade.core.behaviours.OneShotBehaviour;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Metering.*;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.TransitionKeys.Metering.NO_PANIC;

// TODO: Make sure ALL FIELDS ARE RESET by calling onStart()
public final class CalculateEnergyBalanceBehaviour extends OneShotBehaviour {
    private final CommunityCoordinatorAgent agent;
    private int result = NO_PANIC;

    public CalculateEnergyBalanceBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void onStart() {
        result = NO_PANIC;
    }

    @Override
    public void action() {
        Double currentCharge = (Double) getDataStore().get(CURRENT_CHARGE);
        Double powerProduced = (Double) getDataStore().get(POWER_PRODUCED);

        agent.updatePredictionModel(powerProduced, currentCharge);
        agent.updateRunningAverage(powerProduced);

        final double allocatedThisTick = agent.getAllocatedAt(agent.tick);
        final double availableEnergy = currentCharge + powerProduced;
        final double shortfall = allocatedThisTick - availableEnergy;

        agent.log(String.format("Allocated: %.2f, Available: %.2f, Shortfall: %.2f",
                allocatedThisTick, availableEnergy, shortfall), LogSeverity.INFO, this);

        getDataStore().put(AVAILABLE_ENERGY, availableEnergy);
        getDataStore().put(SHORTFALL, shortfall);

        if (agent.shouldTriggerPanic(shortfall, currentCharge)) {
            result = TransitionKeys.Metering.HAS_PANIC;
            agent.log("Energy panic triggered! Shortfall: " + shortfall, LogSeverity.WARN, this);
        }
    }

    @Override
    public int onEnd() {
        return result;
    }
}