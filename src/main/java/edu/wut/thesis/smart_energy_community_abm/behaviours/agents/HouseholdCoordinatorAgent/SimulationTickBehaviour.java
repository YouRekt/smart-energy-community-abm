package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseFSMBehaviour;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.Phase.*;

public final class SimulationTickBehaviour extends BaseFSMBehaviour {

    public SimulationTickBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);
    }
}