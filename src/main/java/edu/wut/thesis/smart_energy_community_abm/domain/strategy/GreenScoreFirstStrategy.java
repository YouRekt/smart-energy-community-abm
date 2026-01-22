package edu.wut.thesis.smart_energy_community_abm.domain.strategy;

public final class GreenScoreFirstStrategy implements NegotiationStrategy {
    private static final double GREENSCORE_WEIGHT = 0.80;
    private static final double COOPERATION_WEIGHT = 0.20;

    private static final double POST_GREENSCORE_WEIGHT = 0.60;
    private static final double POST_COOPERATION_WEIGHT = 0.15;
    private static final double POST_ENERGY_WEIGHT = 0.25;

    private static final double ENERGY_SCALE = 500.0;

    private static final double PANIC_BUFFER = 150.0;
    private static final double BATTERY_LOW_THRESHOLD = 0.40;
    private static final double BATTERY_HIGH_THRESHOLD = 0.85;
    private static final double BATTERY_LOW_BUFFER_MULTIPLIER = 2.0;
    private static final double BATTERY_HIGH_BUFFER_MULTIPLIER = 0.7;

    @Override
    public String getName() {
        return "GreenScoreFirst";
    }

    @Override
    public double computeGenericPriority(double greenScore, double cooperationScore) {
        return (greenScore * GREENSCORE_WEIGHT) + (cooperationScore * COOPERATION_WEIGHT);
    }

    @Override
    public double computeNegotiationPriority(double greenScore, double cooperationScore, long leadTime, long taskDuration) {
        // Timing intentionally ignored - pure fairness focus
        return computeGenericPriority(greenScore, cooperationScore);
    }

    @Override
    public double computePostponementPriority(double greenScore, double cooperationScore, double energyToFree) {
        // Strongly protect high green score households
        // Prefer postponing large consumers among low-green households
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
            // Very aggressive when battery low - protect future fairness
            effectiveBuffer *= BATTERY_LOW_BUFFER_MULTIPLIER;
        } else if (batteryRatio > BATTERY_HIGH_THRESHOLD) {
            // Slightly more lenient when battery healthy
            effectiveBuffer *= BATTERY_HIGH_BUFFER_MULTIPLIER;
        }

        return shortfall > -effectiveBuffer;
    }
}