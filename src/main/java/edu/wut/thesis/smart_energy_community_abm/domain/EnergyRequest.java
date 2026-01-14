package edu.wut.thesis.smart_energy_community_abm.domain;

import jade.core.AID;

public record EnergyRequest(
        AID applianceAID,
        long startTick,
        int duration,
        double energyPerTick
) {
    public boolean isActive(long tick) {
        return tick >= startTick && tick < (startTick + duration);
    }

    public long endTick() {
        return startTick + duration - 1;
    }
}
