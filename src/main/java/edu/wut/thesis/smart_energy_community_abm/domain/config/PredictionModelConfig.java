package edu.wut.thesis.smart_energy_community_abm.domain.config;

// TODO: What is the best way to handle different prediction models configs?
public record PredictionModelConfig(
        String name,
        Double minBatteryChargeThreshold,
        Double productionSafetyFactor,
        Integer windowSize
) {
    public PredictionModelConfig {
        if (name == null || name.isBlank()) {
            name = "MovingAverage";
        }
        if (minBatteryChargeThreshold == null) {
            minBatteryChargeThreshold = 0.2;
        }
        if (windowSize == null) {
            windowSize = 50;
        }

        if (minBatteryChargeThreshold < 0.0 || minBatteryChargeThreshold > 1.0) {
            throw new IllegalArgumentException("minBatteryChargeThreshold must be between 0.0 and 1.0");
        }
        if (windowSize <= 0) {
            throw new IllegalArgumentException("windowSize must be positive");
        }
        if (productionSafetyFactor < 0.0 || productionSafetyFactor > 1.0) {
            throw new IllegalArgumentException("productionSafetyFactor must be between 0.0 and 1.0");
        }
    }
}