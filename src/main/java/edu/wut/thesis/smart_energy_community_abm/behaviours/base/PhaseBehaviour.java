package edu.wut.thesis.smart_energy_community_abm.behaviours.base;

import edu.wut.thesis.smart_energy_community_abm.agents.BaseAgent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;

public abstract class PhaseBehaviour extends SequentialBehaviour {
    protected final BaseAgent agent;

    public PhaseBehaviour(BaseAgent agent) {
        super(agent);
        this.agent = agent;
    }

    protected void setupSubBehaviours(Behaviour[] subBehaviours) {
        for (Behaviour subBehaviour : subBehaviours) {
            subBehaviour.setDataStore(getDataStore());
            addSubBehaviour(subBehaviour);
        }
    }
}
