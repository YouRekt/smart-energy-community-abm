package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityBatteryAgent.Phase1;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityBatteryAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import jade.lang.acl.ACLMessage;

public final class ProcessTickBehaviour extends BaseMessageHandlerBehaviour {
    private boolean receivedTick = false;

    public ProcessTickBehaviour(CommunityBatteryAgent agent) {
        super(agent);
    }

    @Override
    public void onStart() {
        receivedTick = false;
    }

    @Override
    protected void handleInform(ACLMessage msg) {
        receivedTick = true;
        final ACLMessage reply = msg.createReply(ACLMessage.CONFIRM);
        reply.setOntology(CommunityBatteryAgent.class.getSimpleName());
        agent.send(reply);
    }

    @Override
    public boolean done() {
        return receivedTick;
    }
}
