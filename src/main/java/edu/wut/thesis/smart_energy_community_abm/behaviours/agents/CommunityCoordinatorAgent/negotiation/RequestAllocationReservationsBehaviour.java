package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.negotiation;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseFSMBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.OneShotBehaviour;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Negotiation.*;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.TransitionKeys.Negotiation.*;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REFUSE;

public final class RequestAllocationReservationsBehaviour extends BaseFSMBehaviour {
    private static final String INITIAL_SORT = "initial-sort";
    private static final String PROCESS_RESPONSE = "process-response";
    private static final String RESPOND = "respond";
    private static final String EXIT = "exit";
    private static final String ACKNOWLEDGE = "acknowledge";
    private final CommunityCoordinatorAgent agent;

    public RequestAllocationReservationsBehaviour(CommunityCoordinatorAgent agent, DataStore dataStore) {
        super(agent);
        this.agent = agent;
        setDataStore(dataStore);

        registerFirstState(new CollectInitialHouseholdResponsesBehaviour(agent), INITIAL_SORT);
        registerState(new RespondToHouseholdsRequestBehaviour(agent), RESPOND);
        registerState(new ProcessHouseholdResponseBehaviour(agent), PROCESS_RESPONSE);
        registerState(new AcknowledgeAllocationSuccessBehaviour(agent), ACKNOWLEDGE);
        registerLastState(new OneShotBehaviour() {
            @Override
            public void action() {
                agent.log("Finished negotiation with households", LogSeverity.DEBUG, this);
            }
        }, EXIT);

        registerTransition(INITIAL_SORT, RESPOND, INFORM);
        registerTransition(INITIAL_SORT, EXIT, FINISHED);
        registerTransition(RESPOND, ACKNOWLEDGE, NOT_OVERLOADED);
        registerTransition(RESPOND, PROCESS_RESPONSE, OVERLOADED);
        registerTransition(PROCESS_RESPONSE, RESPOND, REFUSE);
        registerTransition(PROCESS_RESPONSE, RESPOND, INFORM);
        registerTransition(PROCESS_RESPONSE, EXIT, FINISHED);
        registerTransition(ACKNOWLEDGE, RESPOND, NEXT_HOUSEHOLD);
        registerTransition(ACKNOWLEDGE, EXIT, FINISHED);
    }

    @Override
    public void onStart() {
        super.onStart();

        getDataStore().put(OVERLOADED_TICKS, null);
        getDataStore().put(HOUSEHOLD_REQUESTS_MAP, null);
        getDataStore().put(AGENT_LIST, null);
        getDataStore().put(HOUSEHOLD_RESPONSE, null);
    }
}
