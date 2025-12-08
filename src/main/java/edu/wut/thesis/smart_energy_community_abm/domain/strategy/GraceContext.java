package edu.wut.thesis.smart_energy_community_abm.domain.strategy;

import jade.core.AID;

public record GraceContext(
        AID householdId,
        double greenScore,
        double requestedEnergy,
        double remainingShortfall,
        double batteryCharge
) {}