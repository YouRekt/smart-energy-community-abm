package edu.wut.thesis.smart_energy_community_abm.agents;

import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityBatteryAgent.SimulationTickBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.MessageSubject;
import edu.wut.thesis.smart_energy_community_abm.domain.TopicHelper;
import jade.core.ServiceException;

public final class CommunityBatteryAgent extends BaseAgent {
    public Double maxCapacity;
    public Double currentCharge;

    @Override
    protected void setup() {
        super.setup();

        final Object[] args = getArguments();

        maxCapacity = (Double) args[0];

        if (maxCapacity == null) {
            throw new IllegalArgumentException("Capacity parameter is null");
        }

        currentCharge = (Double) args[1];

        if (currentCharge == null) {
            throw new IllegalArgumentException("Current charge parameter is null");
        }

        try {
            TopicHelper.registerTopic(this, MessageSubject.TICK);
        } catch (final ServiceException e) {
            throw new RuntimeException(e);
        }

        addBehaviour(new SimulationTickBehaviour(this));
    }
}
