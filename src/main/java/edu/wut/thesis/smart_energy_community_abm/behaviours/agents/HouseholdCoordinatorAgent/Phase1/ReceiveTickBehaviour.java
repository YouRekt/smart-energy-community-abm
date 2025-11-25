package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase1;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import jade.lang.acl.ACLMessage;

public class ReceiveTickBehaviour extends BaseMessageHandlerBehaviour {
    public static final String TICK_MSG = "tick-msg";
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
        agent.tick = Long.parseLong(msg.getContent());
        getDataStore().put(TICK_MSG, msg);
        receivedTick = true;
    }

    @Override
    public boolean done() {
        return receivedTick;
    }
}
