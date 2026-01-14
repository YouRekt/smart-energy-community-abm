package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.metering;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.AllocationEntry;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Date;
import java.util.Map;

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
        for (var applianceAgent : agent.healthyAppliances) {
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.setReplyByDate(replyBy);
            msg.addReceiver(applianceAgent);
            msg.setContent(Double.toString(agent.timetable.getOrDefault(agent.tick, Map.of()).getOrDefault(applianceAgent, new AllocationEntry(0, 0, 0, 0)).requestedEnergy()));
            agent.send(msg);
        }
        getDataStore().put(DataStoreKey.Metering.REQUEST_REPLY_BY, replyBy);
    }
}
