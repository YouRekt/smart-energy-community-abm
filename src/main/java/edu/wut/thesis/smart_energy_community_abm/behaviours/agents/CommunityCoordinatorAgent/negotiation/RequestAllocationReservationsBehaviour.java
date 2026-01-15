package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.negotiation;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseFSMBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.TransitionKeys;
import jade.core.AID;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.OneShotBehaviour;

import java.util.List;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Negotiation.AGENT_LIST;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Negotiation.OVERLOADED_TICKS;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REFUSE;

public final class RequestAllocationReservationsBehaviour extends BaseFSMBehaviour {
    private static final String SEND_REQUEST = "send-request";
    private static final String PROCESS_RESPONSE = "process-response";
    private static final String RESPOND = "respond";
    private static final String EXIT = "exit";
    private static final String ACKNOWLEDGE = "acknowledge";
    private final CommunityCoordinatorAgent agent;

    public RequestAllocationReservationsBehaviour(CommunityCoordinatorAgent agent, DataStore dataStore) {
        super(agent);
        this.agent = agent;
        setDataStore(dataStore);

        registerFirstState(new SendRequestToHouseholdBehaviour(agent), SEND_REQUEST);
        registerState(new ProcessHouseholdResponseBehaviour(agent), PROCESS_RESPONSE);
        registerState(new RespondToHouseholdsRequestBehaviour(agent), RESPOND);
        registerState(new AcknowledgeAllocationSuccessBehaviour(agent), ACKNOWLEDGE);
        registerLastState(new OneShotBehaviour() {
            @Override
            public void action() {
                agent.log("Finished negotiation with household", LogSeverity.DEBUG, this);
            }
        }, EXIT);

        registerDefaultTransition(SEND_REQUEST, PROCESS_RESPONSE);
        registerTransition(PROCESS_RESPONSE, EXIT, REFUSE);
        registerTransition(PROCESS_RESPONSE, RESPOND, INFORM);
        registerTransition(RESPOND, ACKNOWLEDGE, TransitionKeys.Negotiation.NOT_OVERLOADED);
        registerTransition(RESPOND, PROCESS_RESPONSE, TransitionKeys.Negotiation.OVERLOADED);
        registerDefaultTransition(ACKNOWLEDGE, EXIT);
    }

    @Override
    public void onStart() {
        super.onStart();

        final List<AID> agentList = List.copyOf(agent.householdAgents);

        getDataStore().put(AGENT_LIST, agentList);
        getDataStore().put(OVERLOADED_TICKS, null);
    }
}
