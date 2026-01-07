package edu.wut.thesis.smart_energy_community_abm.behaviours.base;

import edu.wut.thesis.smart_energy_community_abm.agents.BaseAgent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;

public abstract class BaseFSMBehaviour extends FSMBehaviour {
    public BaseFSMBehaviour(BaseAgent agent) {
        super(agent);
    }

    @Override
    public void registerState(Behaviour state, String name) {
        super.registerState(state, name);
        state.setDataStore(getDataStore());
    }

    @Override
    public void registerFirstState(Behaviour state, String name) {
        super.registerFirstState(state, name);
        state.setDataStore(getDataStore());
    }

    @Override
    public void registerLastState(Behaviour state, String name) {
        super.registerLastState(state, name);
        state.setDataStore(getDataStore());
    }

    protected void addTransition(String fromBehaviour, String toBehaviour) {
        registerDefaultTransition(fromBehaviour, toBehaviour, new String[]{fromBehaviour});
    }
}
