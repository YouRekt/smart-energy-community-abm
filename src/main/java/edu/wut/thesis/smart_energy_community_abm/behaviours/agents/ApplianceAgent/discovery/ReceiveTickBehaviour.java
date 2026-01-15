package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.discovery;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import jade.lang.acl.ACLMessage;

public final class ReceiveTickBehaviour extends BaseMessageHandlerBehaviour {
    private final ApplianceAgent agent;
    private boolean receivedTick = false;

    public ReceiveTickBehaviour(ApplianceAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void onStart() {
        receivedTick = false;
    }

    @Override
    protected void handleInform(ACLMessage msg) {
        receivedTick = true;
        agent.tick = Long.parseLong(msg.getContent());
        final ACLMessage reply = msg.createReply(ACLMessage.CONFIRM);
        reply.setOntology(ApplianceAgent.class.getSimpleName());
        reply.setContent("I'm responsive!");
        agent.send(reply);
    }

    @Override
    public boolean done() {
        return receivedTick;
    }
}
