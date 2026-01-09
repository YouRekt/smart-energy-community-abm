package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.negotiation;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey;
import jade.lang.acl.ACLMessage;

public class CollectAllocationNegotiationStartRequestBehaviour extends BaseMessageHandlerBehaviour {
    private boolean msgReceived = false;

    public CollectAllocationNegotiationStartRequestBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);
    }

    @Override
    public void onStart() {
        msgReceived = false;
    }

    @Override
    protected void handleRequest(ACLMessage msg) {
        msgReceived = true;
        getDataStore().put(DataStoreKey.Negotiation.ALLOCATION_REQUEST, msg);
    }

    @Override
    public boolean done() {
        return msgReceived;
    }
}
