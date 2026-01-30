package edu.wut.thesis.smart_energy_community_abm.domain.strategy;

public final class AdvancePlanningFirstStrategy implements NegotiationStrategy {
    private static final double GREENSCORE_WEIGHT = 0.30;
    private static final double COOPERATION_WEIGHT = 0.70;

    private static final double NEG_GREENSCORE_WEIGHT = 0.15;
    private static final double NEG_COOPERATION_WEIGHT = 0.25;
    private static final double NEG_LEAD_TIME_WEIGHT = 0.60;

    private static final double POST_GREENSCORE_WEIGHT = 0.15;
    private static final double POST_COOPERATION_WEIGHT = 0.55;
    private static final double POST_ENERGY_WEIGHT = 0.30;

    private static final double LEAD_TIME_SCALE = 15.0;
    private static final double ENERGY_SCALE = 500.0;

    private static final double PANIC_BUFFER = 0.0;
    private static final double BATTERY_CRITICAL_THRESHOLD = 0.10;
    private static final double BATTERY_CRITICAL_BUFFER = 30.0;

    private static final double BASE_ALLOWANCE_PERCENT = 0.05; // 5% Base
    private static final double AGGRESSIVE_SCALAR = 20.0;      // Max 2000% at Score 1.0
    private static final double EXPONENT = 3.0;

    private static final double PANIC_ALLOWANCE_SCALAR = 2.0; // Max 200%
    private static final double PANIC_ALLOWANCE_EXPONENT = 2.0;

    @Override
    public String getName() {
        return "AdvancePlanningFirst";
    }

    @Override
    public double computeGenericPriority(double greenScore, double cooperationScore) {
        return (greenScore * GREENSCORE_WEIGHT) + (cooperationScore * COOPERATION_WEIGHT);
    }

    @Override
    public double computeNegotiationPriority(double greenScore, double cooperationScore, long leadTime, long taskDuration) {
        double leadTimeBonus = 1.0 - Math.exp(-leadTime / LEAD_TIME_SCALE);

        return (greenScore * NEG_GREENSCORE_WEIGHT)
                + (cooperationScore * NEG_COOPERATION_WEIGHT)
                + (leadTimeBonus * NEG_LEAD_TIME_WEIGHT);
    }

    @Override
    public double computePostponementPriority(double greenScore, double cooperationScore, double energyToFree) {
        // Strongly protect cooperative households
        // Among uncooperative ones, prefer postponing large consumers
        double energyFactor = 1.0 / (1.0 + (energyToFree / ENERGY_SCALE));

        return (greenScore * POST_GREENSCORE_WEIGHT)
                + (cooperationScore * POST_COOPERATION_WEIGHT)
                + (energyFactor * POST_ENERGY_WEIGHT);
    }

    @Override
    public double getAllowedGridUsage(double greenScore, double cooperationScore, double averageProduction) {
        double dynamicPercentage = BASE_ALLOWANCE_PERCENT + (AGGRESSIVE_SCALAR * Math.pow(cooperationScore, EXPONENT));

        return averageProduction * dynamicPercentage;
    }

    @Override
    public double getPanicGridAllowance(double greenScore, double cooperationScore, double averageProduction) {
        double dynamicPercentage = PANIC_ALLOWANCE_SCALAR * Math.pow(cooperationScore, PANIC_ALLOWANCE_EXPONENT);

        return averageProduction * dynamicPercentage;
    }

    @Override
    public boolean shouldTriggerPanic(double shortfall, double batteryCharge, double batteryCapacity) {
        double batteryRatio = batteryCharge / batteryCapacity;

        // Only intervene at critical battery levels
        if (batteryRatio < BATTERY_CRITICAL_THRESHOLD) {
            return shortfall > -BATTERY_CRITICAL_BUFFER;
        }

        // Otherwise, only trigger on actual deficit (shortfall > 0)
        return shortfall > -PANIC_BUFFER;
    }
}