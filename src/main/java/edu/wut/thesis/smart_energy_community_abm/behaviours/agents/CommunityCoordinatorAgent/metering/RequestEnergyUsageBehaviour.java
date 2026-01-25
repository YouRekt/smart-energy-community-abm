package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.metering;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Date;

import static edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent.REPLY_BY_DELAY;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Metering.AVAILABLE_ENERGY;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Metering.REQUEST_REPLY_BY;

public final class RequestEnergyUsageBehaviour extends OneShotBehaviour {
    private final CommunityCoordinatorAgent agent;

    public RequestEnergyUsageBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void action() {
        Date replyBy = new Date(System.currentTimeMillis() + REPLY_BY_DELAY);
        Double availableGreenEnergy = (Double) getDataStore().get(AVAILABLE_ENERGY);
        agent.log("Available green energy for tick " + agent.tick + " is " + availableGreenEnergy, LogSeverity.DEBUG, this);
        for (var householdAgent : agent.householdAgents.stream()
                .sorted((x, y) ->
                agent.computeGenericPriority(y)
                .compareTo(agent.computeGenericPriority(x)))
                .toList())
        {
            double householdAllocated = agent.getAllocatedAtFor(agent.tick, householdAgent);
            double greenEnergyAllowed = Math.min(householdAllocated, availableGreenEnergy);
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.setReplyByDate(replyBy);
            msg.addReceiver(householdAgent);
            msg.setContent(Double.toString(greenEnergyAllowed));
            availableGreenEnergy -= greenEnergyAllowed;
            agent.send(msg);
        }
        getDataStore().put(REQUEST_REPLY_BY, replyBy);
    }
}
