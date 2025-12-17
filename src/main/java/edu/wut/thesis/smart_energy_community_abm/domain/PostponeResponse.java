package edu.wut.thesis.smart_energy_community_abm.domain;

import jade.core.AID;

import java.util.Map;

public record PostponeResponse(
        AID householdId,
        boolean accepted,
        Map<Long,Double> energyFreed
) { }