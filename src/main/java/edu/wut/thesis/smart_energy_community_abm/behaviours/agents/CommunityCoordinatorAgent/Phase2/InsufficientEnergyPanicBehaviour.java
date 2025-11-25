package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import jade.core.behaviours.SimpleBehaviour;

// TODO: Implement
public final class InsufficientEnergyPanicBehaviour extends SimpleBehaviour {
    private final CommunityCoordinatorAgent agent;

    public InsufficientEnergyPanicBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void action() {
        if (!agent.energyPanic)
            return;
    }

    @Override
    public boolean done() {
        return !agent.energyPanic;
    }
}
