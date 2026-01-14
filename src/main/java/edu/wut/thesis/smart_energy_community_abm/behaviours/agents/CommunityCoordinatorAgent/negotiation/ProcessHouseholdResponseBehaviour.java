package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.negotiation;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey;
import jade.lang.acl.ACLMessage;

import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REFUSE;

public class ProcessHouseholdResponseBehaviour extends BaseMessageHandlerBehaviour {
    private boolean refused = false;
    private boolean msgReceived = false;

    public ProcessHouseholdResponseBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
    }

    @Override
    public void onStart() {
        refused = false;
        msgReceived = false;
    }

    @Override
    protected void handleRefuse(ACLMessage msg) {
        msgReceived = true;
        refused = true;
    }

    @Override
    protected void handleInform(ACLMessage msg) {
        msgReceived = true;
        getDataStore().put(DataStoreKey.Negotiation.HOUSEHOLD_RESPONSE, msgReceived);
    }

    @Override
    public boolean done() {
        return msgReceived;
    }

    @Override
    public int onEnd() {
        return refused ?
                REFUSE :
                INFORM;
    }
}
