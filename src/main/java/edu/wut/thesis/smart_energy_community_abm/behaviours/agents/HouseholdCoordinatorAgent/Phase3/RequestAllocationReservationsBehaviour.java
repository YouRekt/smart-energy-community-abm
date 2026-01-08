package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase3;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseFSMBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.OneShotBehaviour;

import static jade.lang.acl.ACLMessage.*;

public class RequestAllocationReservationsBehaviour extends BaseFSMBehaviour {
    public static final String CALCULATE_TIMETABLE = "calculate-timetable";
    private static final String EXIT = "exit";
    public static final String COLLECT_RESPONSE = "process-response";
    public static final String RESPOND = "respond";

    public RequestAllocationReservationsBehaviour(HouseholdCoordinatorAgent agent, DataStore dataStore) {
        super(agent);
        setDataStore(dataStore);

        registerFirstState(new CalculateTimetableAllocationsBehaviour(agent), CALCULATE_TIMETABLE);
        registerState(new CollectCommunityResponseBehaviour(agent), COLLECT_RESPONSE);
        registerState(new RespondToCommunityRequestBehaviour(agent), RESPOND);
        registerLastState(new OneShotBehaviour() {
            @Override
            public void action() {
                agent.log("Finished Allocating", LogSeverity.DEBUG, this);
            }
        }, EXIT);

        registerTransition(CALCULATE_TIMETABLE, EXIT, REFUSE);
        registerTransition(CALCULATE_TIMETABLE, COLLECT_RESPONSE, INFORM);
        registerTransition(COLLECT_RESPONSE, RESPOND, CONFIRM);
        registerTransition(COLLECT_RESPONSE, CALCULATE_TIMETABLE, INFORM);
    }
}