package edu.wut.thesis.smart_energy_community_abm.domain;

public record ApplianceTaskInstance(
        ApplianceTask task,
        long startTick,
        int remainingTicks
) {
    public long endTick() {
        return startTick + task.duration() - 1;
    }

    public boolean isActiveAt(long tick) {
        return tick >= startTick && tick < endTick();
    }
}
