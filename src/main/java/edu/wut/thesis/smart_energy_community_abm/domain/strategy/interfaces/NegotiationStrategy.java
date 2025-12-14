package edu.wut.thesis.smart_energy_community_abm.domain.strategy.interfaces;

import edu.wut.thesis.smart_energy_community_abm.domain.strategy.*;

public interface NegotiationStrategy {
    String getName();

    double computePriority(PriorityContext ctx);

    boolean shouldTriggerPanic(PanicContext ctx);
}