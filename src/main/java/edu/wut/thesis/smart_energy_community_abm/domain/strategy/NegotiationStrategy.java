package edu.wut.thesis.smart_energy_community_abm.domain.strategy;

import edu.wut.thesis.smart_energy_community_abm.domain.PanicContext;

public interface NegotiationStrategy {
    String getName();

    double computeNegotiationPriority(double greenScore, double cooperationScore, long firstTaskTick, long requestSpan);

    double computePostponementPriority(double greenScore, double cooperationScore, double energyToFree);

    boolean shouldTriggerPanic(PanicContext ctx);
}