package edu.wut.thesis.smart_energy_community_abm.domain.prediction;

import java.util.LinkedList;
import java.util.Queue;

public final class MovingAveragePredictionModel implements EnergyPredictionModel {
    private final int historyWindowSize;
    private final double batteryCapacity;
    private final double minChargeThreshold;
    private final Queue<Double> productionHistory = new LinkedList<>();

    private double lastKnownBatteryCharge = 0.0;

    /**
     * @param batteryCapacity the total capacity of the battery
     * @param minChargeThreshold the percentage (0.0 - 1.0) of battery to keep as emergency reserve
     * @param windowSize the number of past ticks to consider for the average
     */
    public MovingAveragePredictionModel(double batteryCapacity, double minChargeThreshold, int windowSize) {
        this.batteryCapacity = batteryCapacity;
        this.minChargeThreshold = minChargeThreshold;
        this.historyWindowSize = windowSize;
    }

    @Override
    public String getName() {
        return "MovingAverage";
    }

    @Override
    public void update(double production, double batteryCharge) {
        this.lastKnownBatteryCharge = batteryCharge;

        if (productionHistory.size() >= historyWindowSize) {
            productionHistory.poll();
        }
        productionHistory.add(production);
    }

    @Override
    public double predictAvailableEnergy(long tick) {
        if (productionHistory.isEmpty()) {
            return 1000.0;
        }

        double sum = 0.0;
        for (Double p : productionHistory) {
            sum += p;
        }
        double avgProduction = sum / productionHistory.size();

        double usableBattery = Math.max(0, lastKnownBatteryCharge - (batteryCapacity * minChargeThreshold));
        double batteryContribution = usableBattery * 0.05;

        double prediction = (avgProduction * 0.90) + batteryContribution;

        return Math.max(0.0, prediction);
    }
}
