package edu.wut.thesis.smart_energy_community_abm.domain.strategy;

public final class EnergyVolumeStrategy implements NegotiationStrategy {
    private static final double GREENSCORE_WEIGHT = 0.50;
    private static final double COOPERATION_WEIGHT = 0.50;

    private static final double NEG_GREENSCORE_WEIGHT = 0.20;
    private static final double NEG_COOPERATION_WEIGHT = 0.20;
    private static final double NEG_URGENCY_WEIGHT = 0.25;
    private static final double NEG_DURATION_WEIGHT = 0.35;

    private static final double POST_GREENSCORE_WEIGHT = 0.20;
    private static final double POST_COOPERATION_WEIGHT = 0.20;
    private static final double POST_ENERGY_WEIGHT = 0.60;

    private static final double LEAD_TIME_SCALE = 10.0;
    private static final double DURATION_SCALE = 8.0;
    private static final double ENERGY_SCALE = 300.0;

    private static final double PANIC_BUFFER = 100.0;
    private static final double BATTERY_LOW_THRESHOLD = 0.35;
    private static final double BATTERY_HIGH_THRESHOLD = 0.75;
    private static final double BATTERY_LOW_BUFFER_MULTIPLIER = 2.5;
    private static final double BATTERY_HIGH_BUFFER_MULTIPLIER = 0.6;

    @Override
    public String getName() {
        return "EnergyVolume";
    }

    @Override
    public double computeGenericPriority(double greenScore, double cooperationScore) {
        return (greenScore * GREENSCORE_WEIGHT) + (cooperationScore * COOPERATION_WEIGHT);
    }

    @Override
    public double getAllowedGridUsage(double greenScore, double cooperationScore, double averageProduction) {
        return 0.0;
    }

    @Override
    public double getPanicGridAllowance(double greenScore, double cooperationScore, double averageProduction) {
        return 0.0;
    }

    @Override
    public double computeNegotiationPriority(double greenScore, double cooperationScore, long leadTime, long taskDuration) {
        // Boost urgent tasks (low leadTime = high urgency)
        double urgencyBonus = Math.exp(-leadTime / LEAD_TIME_SCALE);

        // Strong preference for shorter tasks
        double durationFactor = 1.0 / (1.0 + (taskDuration / DURATION_SCALE));

        return (greenScore * NEG_GREENSCORE_WEIGHT)
                + (cooperationScore * NEG_COOPERATION_WEIGHT)
                + (urgencyBonus * NEG_URGENCY_WEIGHT)
                + (durationFactor * NEG_DURATION_WEIGHT);
    }

    @Override
    public double computePostponementPriority(double greenScore, double cooperationScore, double energyToFree) {
        // Heavily prioritize postponing large energy consumers
        // This maximizes impact per postponement for peak smoothing
        // Lower ENERGY_SCALE makes energy difference more impactful
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
            // Very aggressive when battery low - smooth peaks urgently
            effectiveBuffer *= BATTERY_LOW_BUFFER_MULTIPLIER;
        } else if (batteryRatio > BATTERY_HIGH_THRESHOLD) {
            // More lenient when battery healthy
            effectiveBuffer *= BATTERY_HIGH_BUFFER_MULTIPLIER;
        }

        return shortfall > -effectiveBuffer;
    }
}