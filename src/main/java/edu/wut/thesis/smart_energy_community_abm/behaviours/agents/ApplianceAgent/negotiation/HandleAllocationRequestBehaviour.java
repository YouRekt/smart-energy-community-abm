package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.negotiation;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseFSMBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.LogBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.DataStore;

import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REFUSE;

public final class HandleAllocationRequestBehaviour extends BaseFSMBehaviour {
    private static final String EXIT = "exit";

    public HandleAllocationRequestBehaviour(ApplianceAgent agent, DataStore dataStore) {
        super(agent);

        setDataStore(dataStore);

        registerFirstState(new SendAllocationRequestBehaviour(agent), DataStoreKey.Negotiation.SEND_ALLOCATION_REQUEST);
        registerState(new ReceiveAllocationRequestResponseBehaviour(agent), DataStoreKey.Negotiation.COLLECT_ALLOCATION_RESPONSE);
        registerState(new ProcessAllocationRequestResponseBehaviour(agent), DataStoreKey.Negotiation.PROCESS_ALLOCATION_RESPONSE);
        registerLastState(new LogBehaviour(agent, "Leaving HandleAllocationRequestBehaviour", LogSeverity.DEBUG), EXIT);

        registerTransition(DataStoreKey.Negotiation.SEND_ALLOCATION_REQUEST, EXIT, REFUSE);
        registerTransition(DataStoreKey.Negotiation.SEND_ALLOCATION_REQUEST, DataStoreKey.Negotiation.COLLECT_ALLOCATION_RESPONSE, INFORM);
        registerDefaultTransition(DataStoreKey.Negotiation.COLLECT_ALLOCATION_RESPONSE, DataStoreKey.Negotiation.PROCESS_ALLOCATION_RESPONSE);
        registerDefaultTransition(DataStoreKey.Negotiation.PROCESS_ALLOCATION_RESPONSE, EXIT);

    }
}
