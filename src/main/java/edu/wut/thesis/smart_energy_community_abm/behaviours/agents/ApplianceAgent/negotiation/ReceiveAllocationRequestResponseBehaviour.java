package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.negotiation;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import jade.lang.acl.ACLMessage;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Negotiation.ALLOCATION_RESPONSE_MSG;

public final class ReceiveAllocationRequestResponseBehaviour extends BaseMessageHandlerBehaviour {
    private boolean receivedMsg = false;

    public ReceiveAllocationRequestResponseBehaviour(ApplianceAgent agent) {
        super(agent);
    }

    @Override
    public void onStart() {
        receivedMsg = false;
    }

    @Override
    protected void handleConfirm(ACLMessage msg) {
        receivedMsg = true;
        getDataStore().put(ALLOCATION_RESPONSE_MSG, msg);
    }

    @Override
    public boolean done() {
        return receivedMsg;
    }
}

