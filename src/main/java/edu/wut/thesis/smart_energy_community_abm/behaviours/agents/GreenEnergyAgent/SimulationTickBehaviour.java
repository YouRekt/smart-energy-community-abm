package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.GreenEnergyAgent;

import edu.wut.thesis.smart_energy_community_abm.agents.GreenEnergyAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseFSMBehaviour;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.Phase.PHASE_1;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.Phase.PHASE_2;

public final class SimulationTickBehaviour extends BaseFSMBehaviour {
    public SimulationTickBehaviour(GreenEnergyAgent agent) {
        super(agent);
    }
}
