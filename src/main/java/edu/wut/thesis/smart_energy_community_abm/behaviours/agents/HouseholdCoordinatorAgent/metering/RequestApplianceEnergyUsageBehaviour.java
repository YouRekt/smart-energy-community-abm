package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.metering;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Date;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Metering.ENERGY_USAGE_REQUEST_MSG;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Metering.REQUEST_REPLY_BY;

public final class RequestApplianceEnergyUsageBehaviour extends OneShotBehaviour {
    private static final long REPLY_BY_DELAY = 400;

    private final HouseholdCoordinatorAgent agent;

    public RequestApplianceEnergyUsageBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void action() {
        Date replyBy = new Date(System.currentTimeMillis() + REPLY_BY_DELAY);
        double availableGreenEnergy = Double.parseDouble(((ACLMessage) getDataStore().get(ENERGY_USAGE_REQUEST_MSG)).getContent());

        for (var applianceAgent : agent.healthyAppliances) {
            // TODO: Sort Appliances based on their priorities - strategies implementation
            double applianceAllocated = agent.getAllocatedEnergyFor(agent.tick, applianceAgent);
            double greenEnergyAllowed = Math.min(availableGreenEnergy, applianceAllocated);
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.setReplyByDate(replyBy);
            msg.addReceiver(applianceAgent);
            msg.setContent(Double.toString(greenEnergyAllowed));
            agent.send(msg);
        }
        getDataStore().put(REQUEST_REPLY_BY, replyBy);
    }
}
