package edu.wut.thesis.smart_energy_community_abm.domain.strategy;

public interface NegotiationStrategy {
    String getName();

    double computeNegotiationPriority(double greenScore, double cooperationScore, long leadTime, long requestDuration);

    double computePostponementPriority(double greenScore, double cooperationScore, double energyToFree);

    double computeGenericPriority(double greenScore, double cooperationScore);

    boolean shouldTriggerPanic(double shortfall, double batteryCharge, double batteryCapacity);
}