package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.negotiation;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import jade.lang.acl.ACLMessage;

public class CollectCommunityAllocationRequestBehaviour extends BaseMessageHandlerBehaviour {
    public static final String ALLOCATION_REQUEST = "allocation-request";
    private boolean msgReceived = false;

    public CollectCommunityAllocationRequestBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);
    }

    @Override
    public void onStart() {
        msgReceived = false;
    }

    @Override
    protected void handleRequest(ACLMessage msg) {
        msgReceived = true;
        getDataStore().put(ALLOCATION_REQUEST, msg);
    }

    @Override
    public boolean done() {
        return msgReceived;
    }
}
