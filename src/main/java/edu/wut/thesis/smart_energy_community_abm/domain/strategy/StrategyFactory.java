package edu.wut.thesis.smart_energy_community_abm.domain.strategy;

import edu.wut.thesis.smart_energy_community_abm.domain.strategy.implementations.*;
import edu.wut.thesis.smart_energy_community_abm.domain.strategy.interfaces.NegotiationStrategy;

public final class StrategyFactory {
    public static NegotiationStrategy create(String name) {
        if (name == null) {
            return new BalancedStrategy();
        }

        return switch (name) {
            case "GreenScoreFirst" -> new GreenScoreFirstStrategy();
            case "ReservationFirst" -> new ReservationFirstStrategy();
            case "EnergyVolume" -> new EnergyVolumeStrategy();
            default -> new BalancedStrategy();
        };
    }
}