package edu.wut.thesis.smart_energy_community_abm.domain;

import jade.core.AID;

public record AllocationEntry(
        AID requesterId,
        double requestedEnergy,
        double grantedEnergy,
        double priority,
        long requestTimestamp
) { }
