package edu.wut.thesis.smart_energy_community_abm.domain.strategy;

public final class NegotiationStrategyFactory {
    public static NegotiationStrategy create(String name) {
        return switch (name) {
            case "GreenScoreFirst" -> new GreenScoreFirstStrategy();
            case "AdvancePlanningFirstFirst" -> new AdvancePlanningFirstStrategy();
            case "EnergyVolume" -> new EnergyVolumeStrategy();
            case "Naive" -> new NaiveStrategy();
            default -> new BalancedStrategy();
        };
    }
}