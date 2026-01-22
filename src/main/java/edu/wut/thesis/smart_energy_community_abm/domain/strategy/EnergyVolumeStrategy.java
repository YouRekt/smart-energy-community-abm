package edu.wut.thesis.smart_energy_community_abm.domain.strategy;

import edu.wut.thesis.smart_energy_community_abm.domain.PanicContext;

public final class EnergyVolumeStrategy implements NegotiationStrategy {
    private static final double GREENSCORE_WEIGHT = 0.3;
    private static final double COOPERATION_WEIGHT = 0.3;
    private static final double SPAN_WEIGHT = 0.4;
    private static final double ENERGY_WEIGHT = 0.4;
    private static final double SPAN_SCALE = 10.0;
    private static final double ENERGY_SCALE = 500.0; // Scale factor for normalization (e.g., 500 Watts)
    private static final double BUFFER_PERCENTAGE = 0.8;

    @Override
    public String getName() {
        return "EnergyVolume";
    }

    @Override
    public double computeNegotiationPriority(double greenScore, double cooperationScore, long firstTaskTick, long requestSpan) {
        double spanFactor = 1.0 / (1.0 + (requestSpan / SPAN_SCALE));

        return (greenScore * GREENSCORE_WEIGHT)
                + (cooperationScore * COOPERATION_WEIGHT)
                + (spanFactor * SPAN_WEIGHT);
    }

    @Override
    public double computePostponementPriority(double greenScore, double cooperationScore, double energyToFree) {
        double volumeFactor = 1.0 / (1.0 + (energyToFree / ENERGY_SCALE));

        return (greenScore * GREENSCORE_WEIGHT)
                + (cooperationScore * COOPERATION_WEIGHT)
                + (volumeFactor * ENERGY_WEIGHT);
    }

    @Override
    public boolean shouldTriggerPanic(PanicContext ctx) {
        double usableBuffer = ctx.batteryCharge() * (1.0 - ctx.minChargeThreshold());
        return ctx.shortfall() > usableBuffer * BUFFER_PERCENTAGE;
    }
}