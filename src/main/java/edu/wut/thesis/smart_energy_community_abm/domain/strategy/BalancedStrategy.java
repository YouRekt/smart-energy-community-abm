package edu.wut.thesis.smart_energy_community_abm.domain.strategy;

public final class BalancedStrategy implements NegotiationStrategy {
    private static final double GREENSCORE_WEIGHT = 0.50;
    private static final double COOPERATION_WEIGHT = 0.50;

    private static final double NEG_GREENSCORE_WEIGHT = 0.30;
    private static final double NEG_COOPERATION_WEIGHT = 0.30;
    private static final double NEG_LEAD_TIME_WEIGHT = 0.25;
    private static final double NEG_DURATION_WEIGHT = 0.15;

    private static final double POST_GREENSCORE_WEIGHT = 0.30;
    private static final double POST_COOPERATION_WEIGHT = 0.30;
    private static final double POST_ENERGY_WEIGHT = 0.40;

    private static final double LEAD_TIME_SCALE = 20.0;
    private static final double DURATION_SCALE = 10.0;
    private static final double ENERGY_SCALE = 500.0;

    private static final double PANIC_BUFFER = 50.0;
    private static final double BATTERY_LOW_THRESHOLD = 0.30;
    private static final double BATTERY_HIGH_THRESHOLD = 0.80;
    private static final double BATTERY_LOW_BUFFER_MULTIPLIER = 1.5;
    private static final double BATTERY_HIGH_BUFFER_MULTIPLIER = 0.5;

    private static final double BASE_ALLOWANCE_PERCENT = 0.25;
    private static final double BASE_GRID_LIMIT = 3500.0;

    private static final double AGGRESSIVE_SCALAR = 10.0;
    private static final double EXPONENT = 2.0;

    private static final double PANIC_ALLOWANCE_SCALAR = 2.0;
    private static final double PANIC_ALLOWANCE_EXPONENT = 2.0;

    @Override
    public String getName() {
        return "Balanced";
    }

    @Override
    public double computeGenericPriority(double greenScore, double cooperationScore) {
        return (greenScore * GREENSCORE_WEIGHT) + (cooperationScore * COOPERATION_WEIGHT);
    }

    @Override
    public double computeNegotiationPriority(double greenScore, double cooperationScore, long leadTime, long taskDuration) {
        double leadTimeBonus = 1.0 - Math.exp(-leadTime / LEAD_TIME_SCALE);
        double durationFactor = 1.0 / (1.0 + Math.log1p(taskDuration / DURATION_SCALE));

        return (greenScore * NEG_GREENSCORE_WEIGHT)
                + (cooperationScore * NEG_COOPERATION_WEIGHT)
                + (leadTimeBonus * NEG_LEAD_TIME_WEIGHT)
                + (durationFactor * NEG_DURATION_WEIGHT);
    }

    @Override
    public double computePostponementPriority(double greenScore, double cooperationScore, double energyToFree) {
        double energyFactor = 1.0 / (1.0 + (energyToFree / ENERGY_SCALE));

        return (greenScore * POST_GREENSCORE_WEIGHT)
                + (cooperationScore * POST_COOPERATION_WEIGHT)
                + (energyFactor * POST_ENERGY_WEIGHT);
    }

    @Override
    public boolean shouldTriggerPanic(double shortfall, double batteryCharge, double batteryCapacity) {
        double batteryRatio = batteryCharge / batteryCapacity;
        double effectiveBuffer = PANIC_BUFFER;

        if (batteryRatio < BATTERY_LOW_THRESHOLD) {
            effectiveBuffer *= BATTERY_LOW_BUFFER_MULTIPLIER;
        } else if (batteryRatio > BATTERY_HIGH_THRESHOLD) {
            effectiveBuffer *= BATTERY_HIGH_BUFFER_MULTIPLIER;
        }

        return shortfall > -effectiveBuffer;
    }

    @Override
    public double getAllowedGridUsage(double greenScore, double cooperationScore, double averageProduction) {
        // Dynamic Allowance:
        // Score 0.0 -> 25% of AvgProd (Allows ~4kW if AvgProd is 16kW)
        // Score 0.5 -> 25% + (10 * 0.25) = 275% of AvgProd (Huge reward)
        double dynamicPercentage = BASE_ALLOWANCE_PERCENT + (AGGRESSIVE_SCALAR * Math.pow(greenScore, EXPONENT));
        double dynamicAllowance = averageProduction * dynamicPercentage;

        // Static Allowance:
        // Guaranteed floor based on scores.
        // Even with 0.0 score, we give a small fraction of the base limit to avoid "0 allowance" bugs,
        // but realistic scores will quickly get > 3500W.
        // We use Math.max(0.1, score) to ensure a true minimum floor.
        double effectiveScore = Math.max(0.2, (greenScore * 0.5) + (cooperationScore * 0.5));
        double staticAllowance = BASE_GRID_LIMIT * effectiveScore;

        return Math.max(dynamicAllowance, staticAllowance);
    }

    @Override
    public double getPanicGridAllowance(double greenScore, double cooperationScore, double averageProduction) {
        double dynamicPercentage = PANIC_ALLOWANCE_SCALAR * Math.pow(greenScore, PANIC_ALLOWANCE_EXPONENT);
        return averageProduction * dynamicPercentage;
    }
}