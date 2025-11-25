package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import jade.core.behaviours.FSMBehaviour;

import static edu.wut.thesis.smart_energy_community_abm.domain.Phase.*;

public final class SimulationTickBehaviour extends FSMBehaviour {

    public SimulationTickBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);

        registerFirstState(new Phase1Behaviour(agent), PHASE_1);
        registerState(new Phase2Behaviour(agent), PHASE_2);
        registerState(new Phase3Behaviour(agent), PHASE_3);
        registerState(new Phase4Behaviour(agent), PHASE_4);

        addTransition(PHASE_1, PHASE_2);
        addTransition(PHASE_2, PHASE_3);
        addTransition(PHASE_3, PHASE_4);
        addTransition(PHASE_4, PHASE_1);
    }

    private void addTransition(String fromBehaviour, String toBehaviour) {
        registerDefaultTransition(fromBehaviour, toBehaviour, new String[]{fromBehaviour});
    }
}