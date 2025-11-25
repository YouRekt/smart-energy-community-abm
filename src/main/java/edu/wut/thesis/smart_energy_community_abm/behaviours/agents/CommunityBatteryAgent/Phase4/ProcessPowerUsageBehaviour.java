package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityBatteryAgent.Phase4;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityBatteryAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.LogSeverity;
import jade.lang.acl.ACLMessage;

public class ProcessPowerUsageBehaviour extends BaseMessageHandlerBehaviour {
    private final CommunityBatteryAgent agent;
    private boolean receivedUsage = false;

    public ProcessPowerUsageBehaviour(CommunityBatteryAgent agent) {
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
        agent.currentCharge += Double.parseDouble(msg.getContent());
        if (agent.currentCharge <= agent.minChargeThreshold) {
            agent.log("Something went wrong, power went below minimal threshold", LogSeverity.ERROR);
        }
        agent.currentCharge = Math.clamp(agent.currentCharge, 0, agent.capacity);
        final ACLMessage reply =  msg.createReply(ACLMessage.CONFIRM);
        agent.send(reply);
    }

    @Override
    public boolean done() {
        return receivedUsage;
    }
}
