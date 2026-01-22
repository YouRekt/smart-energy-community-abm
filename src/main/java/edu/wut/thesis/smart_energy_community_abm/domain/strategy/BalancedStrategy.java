package edu.wut.thesis.smart_energy_community_abm.domain.strategy;

import edu.wut.thesis.smart_energy_community_abm.domain.PanicContext;

public final class BalancedStrategy implements NegotiationStrategy {
    private static final double GREENSCORE_WEIGHT = 0.5;
    private static final double COOPERATION_WEIGHT = 0.5;

    @Override
    public String getName() {
        return "Balanced";
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
        return ctx.shortfall() > usableBuffer;
    }
}