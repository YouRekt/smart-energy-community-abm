package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import jade.lang.acl.ACLMessage;

public class Phase1Behaviour extends MessageHandlerBehaviour {
    public Phase1Behaviour(ApplianceAgent agent) {
        super(agent);
    }

    @Override
    protected void handleInform(ACLMessage msg) {
        ACLMessage reply = msg.createReply(ACLMessage.CONFIRM);
        agent.send(reply);
    }
}
