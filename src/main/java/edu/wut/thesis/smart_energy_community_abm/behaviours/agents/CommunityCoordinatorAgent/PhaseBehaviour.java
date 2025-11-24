package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;

public abstract class PhaseBehaviour extends SequentialBehaviour {
    protected final CommunityCoordinatorAgent agent;

    public PhaseBehaviour(CommunityCoordinatorAgent agent) {
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
