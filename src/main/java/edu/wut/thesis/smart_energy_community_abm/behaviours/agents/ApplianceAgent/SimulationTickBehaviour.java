package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseFSMBehaviour;

import static edu.wut.thesis.smart_energy_community_abm.domain.Phase.PHASE_1;
import static edu.wut.thesis.smart_energy_community_abm.domain.Phase.PHASE_2;

public final class SimulationTickBehaviour extends BaseFSMBehaviour {
    public SimulationTickBehaviour(ApplianceAgent agent) {
        super(agent);

        registerFirstState(new Phase1Behaviour(agent), PHASE_1);
        registerState(new Phase2Behaviour(agent), PHASE_2);

        addTransition(PHASE_1, PHASE_2);
        addTransition(PHASE_2, PHASE_1);
    }
}
