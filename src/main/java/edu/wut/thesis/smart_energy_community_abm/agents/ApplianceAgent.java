package edu.wut.thesis.smart_energy_community_abm.agents;

import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.SimulationTickBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import edu.wut.thesis.smart_energy_community_abm.domain.messages.TopicHelper;
import jade.core.ServiceException;

public final class ApplianceAgent extends BaseAgent {
    public long tick;
    public boolean insufficientEnergy = false;

    @Override
    protected void setup() {
        super.setup();

        final Object[] args = getArguments();

        final String coordinatorName = (String) args[0];

        if (coordinatorName == null) {
            log("Household Coordinator's name is missing", LogSeverity.ERROR, this);
            doDelete();
            throw new RuntimeException("Household Coordinator's name is missing");
        }

        try {
            TopicHelper.registerTopic(this, coordinatorName);
        } catch (final ServiceException e) {
            throw new RuntimeException(e);
        }

        addBehaviour(new SimulationTickBehaviour(this));
    }
}
