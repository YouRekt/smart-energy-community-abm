package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.GreenEnergyAgent;

import edu.wut.thesis.smart_energy_community_abm.agents.GreenEnergyAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.CyclicFSMBehaviour;
import jade.core.behaviours.Behaviour;

import java.util.List;

public final class SimulationTickBehaviour extends CyclicFSMBehaviour {
    public SimulationTickBehaviour(GreenEnergyAgent agent) {
        super(agent);
    }

    @Override
    protected List<Behaviour> getPhases() {
        GreenEnergyAgent agent = (GreenEnergyAgent) this.myAgent;
        return List.of(
                new Phase1Behaviour(agent),
                new Phase2Behaviour(agent)
        );
    }
}
