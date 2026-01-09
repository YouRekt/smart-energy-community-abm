package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.metering;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.metering.CollectEnergyStatusBehaviour.ENERGY_USAGE_REQUEST_MSG;
import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.metering.ProcessApplianceEnergyUsageBehaviour.EXTERNAL_ENERGY_USED;
import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.metering.ProcessApplianceEnergyUsageBehaviour.GREEN_ENERGY_USED;

public final class RespondToCommunityCoordinatorBehaviour extends OneShotBehaviour {
    private final HouseholdCoordinatorAgent agent;

    public RespondToCommunityCoordinatorBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void action() {
        final ACLMessage msg = (ACLMessage) getDataStore().get(ENERGY_USAGE_REQUEST_MSG);
        final ACLMessage reply  = msg.createReply(ACLMessage.INFORM);
        reply.setContent(String.format("%s,%s", getDataStore().get(GREEN_ENERGY_USED), getDataStore().get(EXTERNAL_ENERGY_USED)));
        agent.send(reply);
    }
}
