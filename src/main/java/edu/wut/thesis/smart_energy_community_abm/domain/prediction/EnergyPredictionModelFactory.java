package edu.wut.thesis.smart_energy_community_abm.domain.prediction;

import java.util.Arrays;

public class EnergyPredictionModelFactory {

    public static EnergyPredictionModel create(String name, Object[] args) {
        if (name == null) {
            name = "MovingAverage";
        }

        switch (name) {
            case "MovingAverage":
            default:
                return createMovingAverageModel(args);
        }
    }

    private static MovingAveragePredictionModel createMovingAverageModel(Object[] args) {
        if (args == null) {
            throw new IllegalArgumentException("Arguments array cannot be null for MovingAveragePredictionModel");
        }

        if (args.length < 3) {
            throw new IllegalArgumentException("MovingAveragePredictionModel requires 3 arguments: " +
                    "[batteryCapacity (Double), minChargeThreshold (Double), windowSize (Integer)]. " +
                    "Received: " + Arrays.toString(args));
        }

        if (args[0] == null) {
            throw new IllegalArgumentException("Argument 0 (Battery Capacity) cannot be null");
        }
        if (!(args[0] instanceof Number)) {
            throw new IllegalArgumentException("Argument 0 (Battery Capacity) must be a Number, got: " + args[0].getClass().getSimpleName());
        }
        double batteryCapacity = ((Number) args[0]).doubleValue();

        if (args[1] == null) {
            throw new IllegalArgumentException("Argument 1 (Min Charge Threshold) cannot be null");
        }
        if (!(args[1] instanceof Number)) {
            throw new IllegalArgumentException("Argument 1 (Min Charge Threshold) must be a Number, got: " + args[1].getClass().getSimpleName());
        }
        double minChargeThreshold = ((Number) args[1]).doubleValue();

        if (args[2] == null) {
            throw new IllegalArgumentException("Argument 2 (Window Size) cannot be null");
        }
        if (!(args[2] instanceof Integer)) {
            throw new IllegalArgumentException("Argument 2 (Window Size) must be an Integer, got: " + args[2].getClass().getSimpleName());
        }
        int windowSize = (Integer) args[2];

        return new MovingAveragePredictionModel(batteryCapacity, minChargeThreshold, windowSize);
    }
}