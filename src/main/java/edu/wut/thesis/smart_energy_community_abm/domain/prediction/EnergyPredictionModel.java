package edu.wut.thesis.smart_energy_community_abm.domain.prediction;

import java.util.Map;
import java.util.function.LongFunction;

public interface EnergyPredictionModel {
    String getName();
    /**
     * Updates the model with the latest observed data from the grid.
     * Should be called once per tick after metering.
     *
     * @param production     Total energy produced by green sources this tick.
     * @param batteryCharge  Current energy stored in the battery.
     */
    void update(double production, double batteryCharge);

    /**
     * Predicts the maximum available energy for the next allocation cycle.
     *
     * @param tick The current simulation tick (useful for time-based/seasonal models).
     * @return The estimated energy amount available for distribution.
     */
    double predictAvailableEnergy(long tick);

    /**
     * Simulates the energy balance over a range of ticks, accounting for cumulative battery usage.
     *
     * @param startTick       The starting tick of the simulation.
     * @param endTick         The ending tick (inclusive).
     * @param loadPerTickProvider A function that returns the allocated load (energy demand) for a given tick.
     * @return A map where: <br/>
     * {@code Key} = tick <br/>
     * {@code Value} = (AllocatedLoad - PredictedAvailable), where if <br/>
     * entry > 0 : Overload (Deficit). <br/>
     * entry < 0 : Surplus (Available capacity).
     */
    Map<Long, Double> simulateEnergyBalances(long startTick, long endTick, LongFunction<Double> loadPerTickProvider);
}
