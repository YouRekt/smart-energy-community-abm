package edu.wut.thesis.smart_energy_community_abm.domain.strategy.implementations;

import edu.wut.thesis.smart_energy_community_abm.domain.strategy.*;
import edu.wut.thesis.smart_energy_community_abm.domain.strategy.interfaces.NegotiationStrategy;

public final class EnergyVolumeStrategy implements NegotiationStrategy {
    private static final double GREENSCORE_WEIGHT = 0.3;
    private static final double RESERVATION_WEIGHT = 0.5;
    private static final double ENERGY_WEIGHT = 0.2;
    private static final double RESERVATION_DECAY = 50.0;
    private static final double ENERGY_SCALE = 100.0;

    @Override
    public String getName() {
        return "EnergyVolume";
    }

    @Override
    public double computePriority(PriorityContext ctx) {
        double greenScoreWeight = 1.0 - ctx.greenScore();
        long reservationAge = ctx.currentTick() - ctx.entry().requestTimestamp();
        double reservationBonus = 1.0 - Math.exp(-reservationAge / RESERVATION_DECAY);

        // Smaller requests get boost
        double energyFactor = 1.0 / (1.0 + Math.log1p(ctx.totalEnergyRequested() / ENERGY_SCALE));

        return (greenScoreWeight * GREENSCORE_WEIGHT)
                + (reservationBonus * RESERVATION_WEIGHT)
                + (energyFactor * ENERGY_WEIGHT);
    }

    @Override
    public boolean shouldGrantBatteryGrace(GraceContext ctx) {
        // More lenient for small requests
        double threshold = 0.5 + (0.2 * Math.max(0, 1.0 - ctx.requestedEnergy() / 1000.0));
        return ctx.greenScore() >= threshold;
    }

    @Override
    public boolean shouldTriggerPanic(PanicContext ctx) {
        // Trigger earlier to maximize gap-filling opportunities
        double usableBuffer = ctx.batteryCharge() * (1.0 - ctx.minChargeThreshold());
        return ctx.shortfall() > usableBuffer * 0.8;
    }
}