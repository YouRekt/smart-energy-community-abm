package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.metering;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Date;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Metering.AVAILABLE_ENERGY;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Metering.REQUEST_REPLY_BY;

public final class RequestEnergyUsageBehaviour extends OneShotBehaviour {
    private static final long REPLY_BY_DELAY = 500;

    private final CommunityCoordinatorAgent agent;

    public RequestEnergyUsageBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void action() {
        Date replyBy = new Date(System.currentTimeMillis() + REPLY_BY_DELAY);
        Double availableGreenEnergy = (Double) getDataStore().get(AVAILABLE_ENERGY);
        for (var householdAgent : agent.householdAgents) {
            // TODO: Sort Households based on their priorities - strategies implementation
            double householdAllocated = agent.getAllocatedAtFor(agent.tick, householdAgent);
            double greenEnergyAllowed = Math.min(householdAllocated, availableGreenEnergy);
            availableGreenEnergy = Math.max(availableGreenEnergy - householdAllocated, 0);
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.setReplyByDate(replyBy);
            msg.addReceiver(householdAgent);
            msg.setContent(Double.toString(greenEnergyAllowed));
            agent.send(msg);
        }
        getDataStore().put(REQUEST_REPLY_BY, replyBy);
    }
}
