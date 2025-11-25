package edu.wut.thesis.smart_energy_community_abm.behaviours.base;

import edu.wut.thesis.smart_energy_community_abm.agents.BaseAgent;
import jade.core.behaviours.FSMBehaviour;

public abstract class BaseFSMBehaviour extends FSMBehaviour {
    public BaseFSMBehaviour(BaseAgent agent) {
        super(agent);
    }

    protected void addTransition(String fromBehaviour, String toBehaviour) {
        registerDefaultTransition(fromBehaviour, toBehaviour, new String[]{fromBehaviour});
    }
}
