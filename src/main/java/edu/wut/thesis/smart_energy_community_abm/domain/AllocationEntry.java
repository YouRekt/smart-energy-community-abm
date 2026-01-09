package edu.wut.thesis.smart_energy_community_abm.domain;

public record AllocationEntry(
        double requestedEnergy,
        long requestTimestamp,
        long allocationStart,
        long duration
) {
    public long allocationEnd()
    {
        return allocationStart + duration - 1;
    }
}
