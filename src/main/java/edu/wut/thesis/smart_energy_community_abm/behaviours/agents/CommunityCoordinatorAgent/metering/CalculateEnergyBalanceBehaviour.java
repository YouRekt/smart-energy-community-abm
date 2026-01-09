package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.metering;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.TransitionKeys;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;

import java.util.Map;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Metering.CURRENT_CHARGE;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Metering.POWER_PRODUCED;

public final class CalculateEnergyBalanceBehaviour extends OneShotBehaviour {
    private final CommunityCoordinatorAgent agent;
    private int result = TransitionKeys.Metering.NO_PANIC;

    public CalculateEnergyBalanceBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void action() {
        Double currentCharge = (Double) getDataStore().get(CURRENT_CHARGE);
        Double powerProduced = (Double) getDataStore().get(POWER_PRODUCED);

        agent.updateRunningAverage(powerProduced);

        final double allocatedThisTick = agent.getAllocatedAt(agent.tick);
        final double availableEnergy = currentCharge + powerProduced;
        final double shortfall = allocatedThisTick - availableEnergy;

        agent.log(String.format("Allocated: %.2f, Available: %.2f, Shortfall: %.2f",
                allocatedThisTick, availableEnergy, shortfall), LogSeverity.DEBUG, this);

        getDataStore().put(DataStoreKey.Metering.AVAILABLE_ENERGY, availableEnergy);
        getDataStore().put(DataStoreKey.Metering.SHORTFALL, shortfall);

        if (shortfall > 0) {
            Map<AID, Double> tickAllocations = agent.allocations.getOrDefault(agent.tick, Map.of());
            int householdsAffected = tickAllocations.size();

            if (agent.shouldTriggerPanic(shortfall, currentCharge, householdsAffected)) {
                result = TransitionKeys.Metering.HAS_PANIC;
                agent.log("Energy panic triggered!", LogSeverity.WARNING, agent);
            }
        }
    }

    @Override
    public int onEnd() {
        return result;
    }
}