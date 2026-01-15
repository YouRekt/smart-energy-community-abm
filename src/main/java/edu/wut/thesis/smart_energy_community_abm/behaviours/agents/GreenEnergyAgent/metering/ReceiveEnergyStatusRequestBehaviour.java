package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.GreenEnergyAgent.metering;

import edu.wut.thesis.smart_energy_community_abm.agents.GreenEnergyAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import jade.lang.acl.ACLMessage;

public final class ReceiveEnergyStatusRequestBehaviour extends BaseMessageHandlerBehaviour {
    private final GreenEnergyAgent agent;
    private boolean receivedRequest = false;

    public ReceiveEnergyStatusRequestBehaviour(GreenEnergyAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void onStart() {
        receivedRequest = false;
    }

    @Override
    protected void handleRequest(ACLMessage msg) {
        receivedRequest = true;
        final ACLMessage reply = msg.createReply(ACLMessage.INFORM);
        final Double producedEnergy = agent.produceEnergy(true);
        reply.setContent(producedEnergy.toString());
        reply.setOntology(GreenEnergyAgent.class.getSimpleName());
        agent.send(reply);
    }

    @Override
    public boolean done() {
        return receivedRequest;
    }
}
