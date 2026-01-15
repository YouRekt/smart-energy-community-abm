package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.metering;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.TimeoutMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.lang.acl.ACLMessage;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Metering.EXTERNAL_ENERGY_USED;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Metering.GREEN_ENERGY_USED;

public final class ProcessEnergyUsageBehaviour extends TimeoutMessageHandlerBehaviour {
    private final CommunityCoordinatorAgent agent;

    private Double greenEnergyUsed;
    private Double externalEnergyUsed;

    public ProcessEnergyUsageBehaviour(CommunityCoordinatorAgent agent) {
        super(agent, DataStoreKey.Metering.REQUEST_REPLY_BY);
        this.agent = agent;
    }

    @Override
    public void onStart() {
        super.onStart();
        greenEnergyUsed = 0.0;
        externalEnergyUsed = 0.0;
        setExpectedResponses(agent.householdAgents.size());
    }

    @Override
    public int onEnd() {
        getDataStore().put(GREEN_ENERGY_USED, greenEnergyUsed);
        getDataStore().put(EXTERNAL_ENERGY_USED, externalEnergyUsed);
        return super.onEnd();
    }

    @Override
    protected void handleInform(ACLMessage msg) {
        if (!isMessageTimely(msg)) {
            agent.log("Received a stale message " + ((msg.getContent() == null) ? "" : msg.getContent()), LogSeverity.WARN, this);
        } else {
            try {
                String[] parts = msg.getContent().split(",");
                greenEnergyUsed += Double.parseDouble(parts[0]);
                externalEnergyUsed += Double.parseDouble(parts[1]);
                incrementReceivedCount();
            } catch (RuntimeException e) {
                agent.log("Error parsing usage: " + msg.getContent(), LogSeverity.ERROR, this);
            }
        }
    }
}
