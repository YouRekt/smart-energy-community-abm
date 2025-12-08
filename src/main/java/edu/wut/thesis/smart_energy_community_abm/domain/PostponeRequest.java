package edu.wut.thesis.smart_energy_community_abm.domain;

import jade.core.AID;

public record PostponeRequest(
        AID householdId,
        long tick,
        double energyAmount,
        double priority
) { }