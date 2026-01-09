package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.CyclicFSMBehaviour;
import jade.core.behaviours.Behaviour;

import java.util.List;

public final class SimulationTickBehaviour extends CyclicFSMBehaviour {

    public SimulationTickBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
    }

    @Override
    protected List<Behaviour> getPhases() {
        CommunityCoordinatorAgent agent = (CommunityCoordinatorAgent) this.myAgent;
        return List.of(
                new Phase1Behaviour(agent),
                new Phase2Behaviour(agent),
                new Phase3Behaviour(agent)
        );
    }
}