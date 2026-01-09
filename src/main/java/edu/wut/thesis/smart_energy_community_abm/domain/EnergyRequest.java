package edu.wut.thesis.smart_energy_community_abm.domain;

public record EnergyRequest(
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
