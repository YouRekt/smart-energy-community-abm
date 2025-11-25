package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityBatteryAgent.Phase2;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityBatteryAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import jade.lang.acl.ACLMessage;

public final class ProcessRequestBehaviour extends BaseMessageHandlerBehaviour {
    private final CommunityBatteryAgent agent;
    private boolean receivedRequest = false;

    public ProcessRequestBehaviour(CommunityBatteryAgent agent) {
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
        reply.setContent(agent.capacity.toString());
        // TODO: Ontology, language, conversation ID, any difference?
        reply.setOntology(CommunityBatteryAgent.class.getSimpleName());
        agent.send(reply);
    }

    @Override
    public boolean done() {
        return receivedRequest;
    }
}
