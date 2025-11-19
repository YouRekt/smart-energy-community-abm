package edu.wut.thesis.smart_energy_community_abm.agents;

import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.MessageHandlerBehaviour;

public class ApplianceAgent extends BaseAgent {
    @Override
    protected void setup() {
        super.setup();

        addBehaviour(new MessageHandlerBehaviour(this));
    }
}
