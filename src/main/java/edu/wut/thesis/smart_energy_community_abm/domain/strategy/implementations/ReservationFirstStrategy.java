package edu.wut.thesis.smart_energy_community_abm.domain.strategy.implementations;

import edu.wut.thesis.smart_energy_community_abm.domain.strategy.*;
import edu.wut.thesis.smart_energy_community_abm.domain.strategy.interfaces.NegotiationStrategy;

public final class ReservationFirstStrategy implements NegotiationStrategy {
    private static final double GREENSCORE_WEIGHT = 0.15;
    private static final double RESERVATION_WEIGHT = 0.7;
    private static final double COOPERATION_WEIGHT = 0.15;
    private static final double RESERVATION_DECAY = 30.0;  // faster saturation rewards early planners more
    private static final double BUFFER_PERCENTAGE = 1.1;

    @Override
    public String getName() {
        return "ReservationFirst";
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
        // Trust that planned loads will fit
        double usableBuffer = ctx.batteryCharge() * (1.0 - ctx.minChargeThreshold());
        return ctx.shortfall() > usableBuffer * BUFFER_PERCENTAGE;
    }
}
