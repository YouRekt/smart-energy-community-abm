package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase3;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import jade.core.behaviours.OneShotBehaviour;

public final class AskForRequestsBehaviour extends OneShotBehaviour {
    private final CommunityCoordinatorAgent agent;

    public AskForRequestsBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void action() {

    }
}
