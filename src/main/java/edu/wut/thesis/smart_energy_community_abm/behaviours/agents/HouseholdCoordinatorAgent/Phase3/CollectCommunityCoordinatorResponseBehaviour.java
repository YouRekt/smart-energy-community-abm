package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase3;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import jade.lang.acl.ACLMessage;

import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase3.CollectAllocationNegotiationStartRequestBehaviour.ALLOCATION_REQUEST;
import static jade.lang.acl.ACLMessage.CONFIRM;
import static jade.lang.acl.ACLMessage.INFORM;

public class CollectCommunityCoordinatorResponseBehaviour extends BaseMessageHandlerBehaviour {
    private boolean msgReceived = false;
    private boolean confirmed = false;

    public CollectCommunityCoordinatorResponseBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);
    }

    @Override
    public void onStart() {
        msgReceived = false;
        confirmed = false;
    }

    @Override
    protected void handleInform(ACLMessage msg) {
        msgReceived = true;
        getDataStore().put(ALLOCATION_REQUEST, msg);
    }

    @Override
    protected void handleConfirm(ACLMessage msg) {
        msgReceived = true;
        confirmed = true;
        getDataStore().put(ALLOCATION_REQUEST, msg);
    }

    @Override
    public int onEnd() {
        return confirmed ?
                CONFIRM :
                INFORM;
    }

    @Override
    public boolean done() {
        return msgReceived;
    }
}
