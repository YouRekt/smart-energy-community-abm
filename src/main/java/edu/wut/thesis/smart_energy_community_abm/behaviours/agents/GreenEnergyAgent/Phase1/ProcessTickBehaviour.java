package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.GreenEnergyAgent.Phase1;

import edu.wut.thesis.smart_energy_community_abm.agents.GreenEnergyAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import jade.lang.acl.ACLMessage;

public class ProcessTickBehaviour extends BaseMessageHandlerBehaviour {
    private final GreenEnergyAgent agent;
    private boolean receivedTick = false;

    public ProcessTickBehaviour(GreenEnergyAgent agent) {
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
        agent.send(reply);
    }

    @Override
    public boolean done() {
        return receivedTick;
    }
}
