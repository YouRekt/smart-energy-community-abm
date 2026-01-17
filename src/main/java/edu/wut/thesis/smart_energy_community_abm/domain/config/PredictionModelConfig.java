package edu.wut.thesis.smart_energy_community_abm.domain.config;

public record PredictionModelConfig(
        String name,
        Double minBatteryChargeThreshold,
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
    }
}