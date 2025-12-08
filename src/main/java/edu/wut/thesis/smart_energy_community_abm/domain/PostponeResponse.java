package edu.wut.thesis.smart_energy_community_abm.domain;

import jade.core.AID;

public record PostponeResponse(
        AID householdId,
        boolean accepted,
        double energyFreed  // 0 if rejected
) { }