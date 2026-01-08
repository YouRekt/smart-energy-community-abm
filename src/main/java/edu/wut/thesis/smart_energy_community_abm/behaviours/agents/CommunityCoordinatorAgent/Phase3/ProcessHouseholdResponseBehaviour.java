package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase3;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import jade.lang.acl.ACLMessage;

import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REFUSE;

public class ProcessHouseholdResponseBehaviour extends BaseMessageHandlerBehaviour {
    public static final String HOUSEHOLD_RESPONSE = "household-response";
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
        final ACLMessage reply = msg.createReply(INFORM);

        agent.send(reply);
    }

    @Override
    protected void handleInform(ACLMessage msg) {
        msgReceived = true;
        getDataStore().put(HOUSEHOLD_RESPONSE, msgReceived);
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
