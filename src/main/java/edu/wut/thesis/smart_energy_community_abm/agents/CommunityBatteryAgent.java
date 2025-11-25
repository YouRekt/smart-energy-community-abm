package edu.wut.thesis.smart_energy_community_abm.agents;

import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityBatteryAgent.SimulationTickBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.MessageSubject;
import edu.wut.thesis.smart_energy_community_abm.domain.TopicHelper;
import jade.core.ServiceException;

public final class CommunityBatteryAgent extends BaseAgent {
    public Double maxCapacity;
    // TODO: Implement this into config
    public Double currentCharge = 10000.0;

    private String coordinatorName;

    @Override
    protected void setup() {
        super.setup();

        final Object[] args = getArguments();

        maxCapacity = (Double) args[0];
        if (maxCapacity == null) {
            throw new IllegalArgumentException("Capacity parameter is null");
        }

        try {
            TopicHelper.registerTopic(this, MessageSubject.TICK);
        } catch (final ServiceException e) {
            throw new RuntimeException(e);
        }

        addBehaviour(new SimulationTickBehaviour(this));
    }
}
