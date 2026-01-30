package edu.wut.thesis.smart_energy_community_abm.domain;

public record ApplianceTask(
        String taskName,
        double humanActivationChance,
        long period,
        boolean postponable,
        int duration,
        double energyPerTick,
        int taskId
) { }
