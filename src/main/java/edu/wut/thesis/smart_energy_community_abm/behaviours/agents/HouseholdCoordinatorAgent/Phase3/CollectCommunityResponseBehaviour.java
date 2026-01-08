package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase3;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import jade.lang.acl.ACLMessage;

import static jade.lang.acl.ACLMessage.CONFIRM;
import static jade.lang.acl.ACLMessage.INFORM;

public class CollectCommunityResponseBehaviour extends BaseMessageHandlerBehaviour {
    public static final String ALLOCATION_REPLY = "allocation-reply";
    private boolean msgReceived = false;
    private boolean confirmed = false;

    public CollectCommunityResponseBehaviour(HouseholdCoordinatorAgent agent) {
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
        getDataStore().put(ALLOCATION_REPLY, msg);
    }

    @Override
    protected void handleConfirm(ACLMessage msg) {
        msgReceived = true;
        confirmed = true;
        getDataStore().put(ALLOCATION_REPLY, msg);
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
