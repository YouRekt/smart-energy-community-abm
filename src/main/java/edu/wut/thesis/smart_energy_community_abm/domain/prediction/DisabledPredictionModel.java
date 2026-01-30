package edu.wut.thesis.smart_energy_community_abm.domain.prediction;

import java.util.HashMap;
import java.util.Map;
import java.util.function.LongFunction;

public final class DisabledPredictionModel implements EnergyPredictionModel {
    public DisabledPredictionModel() {}

    @Override
    public String getName() {
        return "Disabled";
    }

    @Override
    public void update(double production, double batteryCharge) {

    }

    @Override
    public double predictAvailableEnergy(long tick) {
        return Double.MAX_VALUE;
    }

    @Override
    public Map<Long, Double> simulateEnergyBalances(long startTick, long endTick, LongFunction<Double> loadPerTickProvider) {
        Map<Long, Double> balances = new HashMap<>();

        for (long t = startTick; t <= endTick; t++)
            balances.put(t, Double.MIN_VALUE);

        return balances;
    }
}
