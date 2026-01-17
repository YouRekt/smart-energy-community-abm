package edu.wut.thesis.smart_energy_community_abm.domain.strategy;

import edu.wut.thesis.smart_energy_community_abm.domain.PanicContext;
import edu.wut.thesis.smart_energy_community_abm.domain.PriorityContext;

public interface NegotiationStrategy {
    String getName();

    double computePriority(PriorityContext ctx);

    boolean shouldTriggerPanic(PanicContext ctx);
}