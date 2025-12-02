package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityBatteryAgent.Phase2;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityBatteryAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.LogSeverity;
import jade.lang.acl.ACLMessage;

public final class ProcessEnergyUsageBehaviour extends BaseMessageHandlerBehaviour {
    private final CommunityBatteryAgent agent;
    private boolean receivedUsage = false;

    public ProcessEnergyUsageBehaviour(CommunityBatteryAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void onStart() {
        receivedUsage = false;
    }

    @Override
    protected void handleInform(ACLMessage msg) {
        receivedUsage = true;
        agent.currentCharge -= Double.parseDouble(msg.getContent());
        Double deficit = 0.0;
        if (agent.currentCharge <= 0) {
            agent.log("Something went wrong, power went below 0 - responding to coordinator that we need to pull from external grid", LogSeverity.ERROR, this);
            deficit = agent.currentCharge;
        }
        agent.currentCharge = Math.clamp(agent.currentCharge, 0, agent.maxCapacity);
        final ACLMessage reply =  msg.createReply(ACLMessage.INFORM);
        reply.setContent(String.valueOf(Math.abs(deficit)));
        agent.send(reply);
    }

    @Override
    public boolean done() {
        return receivedUsage;
    }
}
