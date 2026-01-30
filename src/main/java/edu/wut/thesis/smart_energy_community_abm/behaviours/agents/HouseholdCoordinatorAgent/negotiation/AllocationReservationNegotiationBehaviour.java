package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.negotiation;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseFSMBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.OneShotBehaviour;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Negotiation.APPLIANCE_CONFIRM_MESSAGES;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Negotiation.CONFIRM_REPLY_BY_DELAY;
import static jade.lang.acl.ACLMessage.*;

public final class AllocationReservationNegotiationBehaviour extends BaseFSMBehaviour {
    private static final String CALCULATE_TIMETABLE = "calculate-timetable";
    private static final String COLLECT_COMMUNITY_RESPONSE = "collect-community-response";
    private static final String FINALIZE = "finalize";
    private static final String EXIT = "exit";
    private static final String ACKNOWLEDGE = "acknowledge";

    public AllocationReservationNegotiationBehaviour(HouseholdCoordinatorAgent agent, DataStore dataStore) {
        super(agent);
        setDataStore(dataStore);

        registerFirstState(new CalculateAllocationTimetableBehaviour(agent), CALCULATE_TIMETABLE);
        registerState(new CollectCommunityCoordinatorResponseBehaviour(agent), COLLECT_COMMUNITY_RESPONSE);
        registerState(new FinalizeAllocationNegotiationBehaviour(agent), FINALIZE);
        registerState(new CollectApplianceAllocationConfirmationBehaviour(agent, CONFIRM_REPLY_BY_DELAY), ACKNOWLEDGE);
        registerLastState(new OneShotBehaviour() {
            @Override
            public void action() {
                agent.log("Finished Allocating", LogSeverity.DEBUG, this);
            }
        }, EXIT);

        registerTransition(CALCULATE_TIMETABLE, EXIT, REFUSE);
        registerTransition(CALCULATE_TIMETABLE, COLLECT_COMMUNITY_RESPONSE, INFORM);
        registerTransition(COLLECT_COMMUNITY_RESPONSE, FINALIZE, CONFIRM);
        registerTransition(COLLECT_COMMUNITY_RESPONSE, CALCULATE_TIMETABLE, INFORM);
        registerDefaultTransition(FINALIZE, ACKNOWLEDGE);
        registerDefaultTransition(ACKNOWLEDGE, EXIT);
    }
}