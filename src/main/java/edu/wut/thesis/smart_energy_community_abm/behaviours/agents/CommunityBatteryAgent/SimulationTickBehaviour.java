package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityBatteryAgent;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityBatteryAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseFSMBehaviour;

import static edu.wut.thesis.smart_energy_community_abm.domain.Phase.*;

public final class SimulationTickBehaviour extends BaseFSMBehaviour {
    public SimulationTickBehaviour(CommunityBatteryAgent agent) {
        super(agent);

        registerFirstState(new Phase1Behaviour(agent), PHASE_1);
        registerState(new Phase2Behaviour(agent), PHASE_2);
        registerState(new Phase4Behaviour(agent), PHASE_4);

        addTransition(PHASE_1, PHASE_2);
        addTransition(PHASE_2, PHASE_4);
        addTransition(PHASE_4, PHASE_1);
    }
}
