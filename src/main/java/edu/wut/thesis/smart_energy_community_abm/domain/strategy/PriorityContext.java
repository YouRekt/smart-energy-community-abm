package edu.wut.thesis.smart_energy_community_abm.domain.strategy;

import edu.wut.thesis.smart_energy_community_abm.domain.AllocationEntry;

public record PriorityContext(
        AllocationEntry entry,
        long currentTick,
        double greenScore,
        double totalEnergyRequested
) {}
