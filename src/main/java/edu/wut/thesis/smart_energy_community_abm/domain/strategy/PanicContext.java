package edu.wut.thesis.smart_energy_community_abm.domain.strategy;

public record PanicContext(
        double shortfall,
        double batteryCharge,
        double minChargeThreshold,
        int householdsAffected
) {}