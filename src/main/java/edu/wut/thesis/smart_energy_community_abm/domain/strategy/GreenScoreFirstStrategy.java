package edu.wut.thesis.smart_energy_community_abm.domain.strategy;

import edu.wut.thesis.smart_energy_community_abm.domain.PanicContext;

public final class GreenScoreFirstStrategy implements NegotiationStrategy {
    private static final double GREENSCORE_WEIGHT = 0.8;
    private static final double COOPERATION_WEIGHT = 0.2;
    private static final double BUFFER_PERCENTAGE = 0.7;

    @Override
    public String getName() {
        return "GreenScoreFirst";
    }

    @Override
    public double computeNegotiationPriority(double greenScore, double cooperationScore, long firstTaskTick, long requestSpan) {
        return (greenScore * GREENSCORE_WEIGHT) + (cooperationScore * COOPERATION_WEIGHT);
    }

    @Override
    public double computePostponementPriority(double greenScore, double cooperationScore, double energyToFree) {
        return (greenScore * GREENSCORE_WEIGHT) + (cooperationScore * COOPERATION_WEIGHT);
    }

    @Override
    public boolean shouldTriggerPanic(PanicContext ctx) {
        double usableBuffer = ctx.batteryCharge() * (1.0 - ctx.minChargeThreshold());
        return ctx.shortfall() > usableBuffer * BUFFER_PERCENTAGE;
    }
}