package edu.wut.thesis.smart_energy_community_abm.domain.strategy;

import edu.wut.thesis.smart_energy_community_abm.domain.PanicContext;

public final class NaiveStrategy implements NegotiationStrategy {
    @Override
    public String getName() {
        return "Naive";
    }

    @Override
    public double computeNegotiationPriority(double greenScore, double cooperationScore, long firstTaskTick, long requestSpan) {
        // Naive Approach: Return constant 0.0.
        // This relies on the system's natural processing order (First Come First Serve)
        // and performs no calculations based on agent scores or task details.
        return 0.0;
    }

    @Override
    public double computePostponementPriority(double greenScore, double cooperationScore, double energyToFree) {
        // Naive Approach: No calculation.
        return 0.0;
    }

    @Override
    public boolean shouldTriggerPanic(PanicContext ctx) {
        // Always return false, even if there is a shortage (never postpone tasks).
        return false;
    }
}