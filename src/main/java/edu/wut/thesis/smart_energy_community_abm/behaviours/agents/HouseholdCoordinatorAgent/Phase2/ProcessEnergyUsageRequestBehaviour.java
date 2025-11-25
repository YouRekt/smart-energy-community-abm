package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase2;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import jade.lang.acl.ACLMessage;

public final class ProcessEnergyUsageRequestBehaviour extends BaseMessageHandlerBehaviour {
    public static final String ENERGY_USAGE_REQUEST_MSG = "energy-usage-request-msg";
    private boolean receivedRequest = false;

    public ProcessEnergyUsageRequestBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);
    }

    @Override
    public void onStart() {
        receivedRequest = false;
    }

    @Override
    protected void handleRequest(ACLMessage msg) {
        receivedRequest = true;
        getDataStore().put(ENERGY_USAGE_REQUEST_MSG, msg);
    }

    @Override
    public boolean done() {
        return receivedRequest;
    }
}
