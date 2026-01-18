package edu.wut.thesis.smart_energy_community_abm.domain.strategy;

public final class NegotiationStrategyFactory {
    public static NegotiationStrategy create(String name) {
        return switch (name) {
            case "GreenScoreFirst" -> new GreenScoreFirstStrategy();
            case "ReservationFirst" -> new ReservationFirstStrategy();
            case "EnergyVolume" -> new EnergyVolumeStrategy();
            default -> new BalancedStrategy();
        };
    }
}