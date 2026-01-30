package edu.wut.thesis.smart_energy_community_abm.domain.prediction;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.function.LongFunction;

public final class MovingAveragePredictionModel implements EnergyPredictionModel {
    private final int historyWindowSize;
    private final double batteryCapacity;
    private final double minChargeThreshold;
    private final double productionSafetyFactor;
    private final Queue<Double> productionHistory = new LinkedList<>();

    private double lastKnownBatteryCharge = 0.0;

    private static final double MAX_BATTERY_DISCHARGE_PERCENT = 0.05; // 5%

    /**
     * @param batteryCapacity the total capacity of the battery
     * @param minChargeThreshold the percentage (0.0 - 1.0) of battery to keep as emergency reserve
     * @param productionSafetyFactor the safety factor (0.0 - 1.0) to multiply production by
     * @param windowSize the number of past ticks to consider for the average
     */
    public MovingAveragePredictionModel(double batteryCapacity, double minChargeThreshold, double productionSafetyFactor, int windowSize) {
        this.batteryCapacity = batteryCapacity;
        this.minChargeThreshold = minChargeThreshold;
        this.historyWindowSize = windowSize;
        this.productionSafetyFactor = productionSafetyFactor;
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
        double avgProduction = calculateAverageProduction();
        double safeProduction = avgProduction * productionSafetyFactor;

        double reserveLimit = batteryCapacity * minChargeThreshold;
        double rawAvailableBattery = Math.max(0, lastKnownBatteryCharge - reserveLimit);

        double maxDischarge = batteryCapacity * MAX_BATTERY_DISCHARGE_PERCENT;
        double usableBattery = Math.min(rawAvailableBattery, maxDischarge);

        return Math.max(0.0, safeProduction + usableBattery);
    }

    @Override
    public Map<Long, Double> simulateEnergyBalances(long startTick, long endTick, LongFunction<Double> loadPerTickProvider) {
        Map<Long, Double> balances = new HashMap<>();

        // 1. Calculate Baseline Production (Constant for this window based on history)
        double avgProduction = calculateAverageProduction();
        double safeProduction = avgProduction * productionSafetyFactor;
        double reserveLimit = batteryCapacity * minChargeThreshold;
        double maxDischarge = batteryCapacity * MAX_BATTERY_DISCHARGE_PERCENT;

        // 2. Initialize Simulated Battery with current real state
        double simulatedBattery = this.lastKnownBatteryCharge;

        // 3. Run Simulation Loop
        for (long t = startTick; t <= endTick; t++) {
            // A. Get the Load for this tick (Existing allocations + potentially new request)
            double load = loadPerTickProvider.apply(t);

            // B. Calculate Available Energy (Production + Usable Battery)
            // Apply both the reserve limit AND the 5% discharge cap
            double rawAvailableBattery = Math.max(0, simulatedBattery - reserveLimit);
            double usableBattery = Math.min(rawAvailableBattery, maxDischarge);

            double totalAvailable = safeProduction + usableBattery;

            // C. Calculate Balance (The metric for the Coordinator)
            // > 0 means we need X more energy (Overload)
            // < 0 means we have X spare energy (Surplus)
            double balance = load - totalAvailable;
            balances.put(t, balance);

            // D. Update Simulated Battery State for the NEXT tick
            // Logic: Net Flow = Production - Load
            // If Production > Load: Battery charges (up to capacity)
            // If Production < Load: Battery drains (down to 0)
            double netFlow = safeProduction - load;
            simulatedBattery += netFlow;

            // Clamp physical limits
            if (simulatedBattery > batteryCapacity) {
                simulatedBattery = batteryCapacity;
            }
            if (simulatedBattery < 0) {
                simulatedBattery = 0;
            }
        }

        return balances;
    }

    public double calculateAverageProduction() {
        if (productionHistory.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (Double p : productionHistory) {
            sum += p;
        }
        return sum / productionHistory.size();
    }
}