package edu.wut.thesis.smart_energy_community_abm.domain.strategy.implementations;

import edu.wut.thesis.smart_energy_community_abm.domain.strategy.*;
import edu.wut.thesis.smart_energy_community_abm.domain.strategy.interfaces.NegotiationStrategy;

public final class BalancedStrategy implements NegotiationStrategy {
    private static final double GREENSCORE_WEIGHT = 0.4;
    private static final double RESERVATION_WEIGHT = 0.6;
    private static final double RESERVATION_DECAY = 50.0;
    private static final double GRACE_THRESHOLD = 0.6;

    @Override
    public String getName() {
        return "Balanced";
    }

    @Override
    public double computePriority(PriorityContext ctx) {
        double greenScoreWeight = 1.0 - ctx.greenScore();
        long reservationAge = ctx.currentTick() - ctx.entry().requestTimestamp();
        double reservationBonus = 1.0 - Math.exp(-reservationAge / RESERVATION_DECAY);

        return (greenScoreWeight * GREENSCORE_WEIGHT) + (reservationBonus * RESERVATION_WEIGHT);
    }

    @Override
    public boolean shouldGrantBatteryGrace(GraceContext ctx) {
        return ctx.greenScore() >= GRACE_THRESHOLD;
    }

    @Override
    public boolean shouldTriggerPanic(PanicContext ctx) {
        double usableBuffer = ctx.batteryCharge() * (1.0 - ctx.minChargeThreshold());
        return ctx.shortfall() > usableBuffer;
    }
}