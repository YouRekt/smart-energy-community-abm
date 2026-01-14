package edu.wut.thesis.smart_energy_community_abm.domain.strategy.implementations;

import edu.wut.thesis.smart_energy_community_abm.domain.strategy.PanicContext;
import edu.wut.thesis.smart_energy_community_abm.domain.strategy.PriorityContext;
import edu.wut.thesis.smart_energy_community_abm.domain.strategy.interfaces.NegotiationStrategy;

public final class BalancedStrategy implements NegotiationStrategy {
    private static final double GREENSCORE_WEIGHT = 0.3;
    private static final double RESERVATION_WEIGHT = 0.5;
    private static final double COOPERATION_WEIGHT = 0.2;
    private static final double RESERVATION_DECAY = 50.0;

    @Override
    public String getName() {
        return "Balanced";
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
        return ctx.shortfall() > usableBuffer;
    }
}