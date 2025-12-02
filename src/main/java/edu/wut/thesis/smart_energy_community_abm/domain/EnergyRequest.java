package edu.wut.thesis.smart_energy_community_abm.domain;

import jade.core.AID;

public record EnergyRequest(
        AID sourceId,
        long startTick,
        int duration,
        double energyPerTick,
        boolean postponable,
        long requestTimestamp
) { }
