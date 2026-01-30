package edu.wut.thesis.smart_energy_community_abm.domain.strategy;

public final class NaiveStrategy implements NegotiationStrategy {
    @Override
    public String getName() {
        return "Naive";
    }

    @Override
    public double computeNegotiationPriority(double greenScore, double cooperationScore, long firstTaskTick, long requestSpan) {
        return 0.0;
    }

    @Override
    public double computePostponementPriority(double greenScore, double cooperationScore, double energyToFree) {
        return 0.0;
    }

    @Override
    public double getAllowedGridUsage(double greenScore, double cooperationScore, double averageProduction) {
        return Double.MAX_VALUE;
    }

    @Override
    public double getPanicGridAllowance(double greenScore, double cooperationScore, double averageProduction) {
        return Double.MAX_VALUE;
    }

    @Override
    public double computeGenericPriority(double greenScore, double cooperationScore) {
        return 0.0;
    }

    @Override
    public boolean shouldTriggerPanic(double shortfall, double batteryCharge, double batteryCapacity) {
        return false;
    }
}