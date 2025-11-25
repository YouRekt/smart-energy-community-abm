package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase1.Phase1Behaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase2.Phase2Behaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase3.Phase3Behaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase4.Phase4Behaviour;
import jade.core.behaviours.FSMBehaviour;

import static edu.wut.thesis.smart_energy_community_abm.domain.Phase.*;

public class SimulationTickBehaviour extends FSMBehaviour {

    public SimulationTickBehaviour(HouseholdCoordinatorAgent agent) {
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