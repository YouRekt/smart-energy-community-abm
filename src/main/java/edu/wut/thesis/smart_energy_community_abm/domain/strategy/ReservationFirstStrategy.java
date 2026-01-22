package edu.wut.thesis.smart_energy_community_abm.domain.strategy;

import edu.wut.thesis.smart_energy_community_abm.domain.PanicContext;

public final class ReservationFirstStrategy implements NegotiationStrategy {
    private static final double GREENSCORE_WEIGHT = 0.2;
    private static final double COOPERATION_WEIGHT = 0.8;
    private static final double TICK_WEIGHT = 0.1;
    private static final double BUFFER_PERCENTAGE = 1.1;

    @Override
    public String getName() {
        return "ReservationFirst";
    }

    @Override
    public double computeNegotiationPriority(double greenScore, double cooperationScore, long firstTaskTick, long requestSpan) {
        double tickFactor = 1.0 / (1.0 + Math.log1p(firstTaskTick % 1000));

        return (greenScore * GREENSCORE_WEIGHT)
                + (cooperationScore * 0.7)
                + (tickFactor * TICK_WEIGHT);
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