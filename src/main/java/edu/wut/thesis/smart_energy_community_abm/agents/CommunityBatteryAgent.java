package edu.wut.thesis.smart_energy_community_abm.agents;

import edu.wut.thesis.smart_energy_community_abm.domain.MessageSubject;
import edu.wut.thesis.smart_energy_community_abm.domain.TopicHelper;
import jade.core.ServiceException;

public final class CommunityBatteryAgent extends BaseAgent {
    private Double capacity;
    private Double minChargeThreshold;  // Hard limit

    private String coordinatorName;

    @Override
    protected void setup() {
        super.setup();

        final Object[] args = getArguments();

        capacity = (Double) args[0];
        if (capacity == null) {
            throw new IllegalArgumentException("Capacity parameter is null");
        }

        minChargeThreshold = (Double) args[1];
        if (minChargeThreshold == null) {
            throw new IllegalArgumentException("Minimum charge threshold parameter is null");
        }

        try {
            TopicHelper.registerTopic(this, MessageSubject.TICK);
        } catch (final ServiceException e) {
            throw new RuntimeException(e);
        }


    }
}
