package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2.ProcessEnergyUsageBehaviour.GREEN_ENERGY_USED;

public final class ReportUsageToBatteryBehaviour extends OneShotBehaviour {
    private final CommunityCoordinatorAgent agent;

    public ReportUsageToBatteryBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void action() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(agent.batteryAgent);
        msg.setContent(getDataStore().get(GREEN_ENERGY_USED).toString());
        agent.send(msg);
    }
}
