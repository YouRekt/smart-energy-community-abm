package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase3;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseFSMBehaviour;
import jade.core.behaviours.DataStore;

public class RequestAllocationReservationsBehaviour extends BaseFSMBehaviour {
    private final HouseholdCoordinatorAgent agent;

    public RequestAllocationReservationsBehaviour(HouseholdCoordinatorAgent agent, DataStore dataStore) {
        super(agent);
        this.agent = agent;
        setDataStore(dataStore);


    }
}
