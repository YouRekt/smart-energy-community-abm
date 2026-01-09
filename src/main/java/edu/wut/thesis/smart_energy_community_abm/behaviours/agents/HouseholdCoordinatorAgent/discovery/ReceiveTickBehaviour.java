package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.discovery;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import jade.lang.acl.ACLMessage;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Discovery.TICK_MSG;

public final class ReceiveTickBehaviour extends BaseMessageHandlerBehaviour {
    private final HouseholdCoordinatorAgent agent;

    private boolean receivedTick = false;

    public ReceiveTickBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void onStart() {
        receivedTick = false;
    }

    @Override
    protected void handleInform(ACLMessage msg) {
        receivedTick = true;
        agent.tick = Long.parseLong(msg.getContent());
        getDataStore().put(TICK_MSG, msg);
    }

    @Override
    public boolean done() {
        return receivedTick;
    }
}
