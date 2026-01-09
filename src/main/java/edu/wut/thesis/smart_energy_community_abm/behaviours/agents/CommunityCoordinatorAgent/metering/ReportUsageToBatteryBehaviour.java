package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.metering;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.metering.CollectEnergyStatusBehaviour.POWER_PRODUCED;
import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.metering.ProcessEnergyUsageBehaviour.GREEN_ENERGY_USED;

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
        msg.setContent(((Double) ((Double) getDataStore().get(GREEN_ENERGY_USED) - (Double) getDataStore().get(POWER_PRODUCED))).toString());
        agent.send(msg);
    }
}
