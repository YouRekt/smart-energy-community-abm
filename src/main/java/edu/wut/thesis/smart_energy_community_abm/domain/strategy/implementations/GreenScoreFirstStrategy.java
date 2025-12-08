package edu.wut.thesis.smart_energy_community_abm.domain.strategy.implementations;

import edu.wut.thesis.smart_energy_community_abm.domain.strategy.*;
import edu.wut.thesis.smart_energy_community_abm.domain.strategy.interfaces.NegotiationStrategy;

public final class GreenScoreFirstStrategy implements NegotiationStrategy {
    private static final double GREENSCORE_WEIGHT = 0.8;
    private static final double RESERVATION_WEIGHT = 0.2;
    private static final double RESERVATION_DECAY = 50.0;
    private static final double GRACE_THRESHOLD = 0.7;

    @Override
    public String getName() {
        return "GreenScoreFirst";
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
        // Only help those really struggling
        return ctx.greenScore() >= GRACE_THRESHOLD;
    }

    @Override
    public boolean shouldTriggerPanic(PanicContext ctx) {
        // Trigger earlier to protect struggling households
        double usableBuffer = ctx.batteryCharge() * (1.0 - ctx.minChargeThreshold());
        return ctx.shortfall() > usableBuffer * 0.7;
    }
}