package edu.wut.thesis.smart_energy_community_abm.domain.strategy;

import edu.wut.thesis.smart_energy_community_abm.domain.PanicContext;
import edu.wut.thesis.smart_energy_community_abm.domain.PriorityContext;

public final class GreenScoreFirstStrategy implements NegotiationStrategy {
    private static final double GREENSCORE_WEIGHT = 0.6;
    private static final double RESERVATION_WEIGHT = 0.25;
    private static final double COOPERATION_WEIGHT = 0.15;
    private static final double RESERVATION_DECAY = 50.0;
    private static final double BUFFER_PERCENTAGE = 0.7;

    @Override
    public String getName() {
        return "GreenScoreFirst";
    }

    @Override
    public double computePriority(PriorityContext ctx) {
        double greenScoreWeight = 1.0 - ctx.greenScore();
        double cooperationWeight = ctx.cooperationScore();

        long reservationAge = ctx.currentTick() - ctx.entry().requestTimestamp();
        double reservationBonus = 1.0 - Math.exp(-reservationAge / RESERVATION_DECAY);

        return (greenScoreWeight * GREENSCORE_WEIGHT)
                + (reservationBonus * RESERVATION_WEIGHT)
                + (cooperationWeight * COOPERATION_WEIGHT);
    }

    @Override
    public boolean shouldTriggerPanic(PanicContext ctx) {
        double usableBuffer = ctx.batteryCharge() * (1.0 - ctx.minChargeThreshold());
        return ctx.shortfall() > usableBuffer * BUFFER_PERCENTAGE;
    }
}