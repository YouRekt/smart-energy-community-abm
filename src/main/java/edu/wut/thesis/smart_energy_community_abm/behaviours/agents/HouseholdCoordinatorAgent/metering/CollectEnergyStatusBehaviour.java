package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.metering;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import jade.lang.acl.ACLMessage;

import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.metering.HandleEnergyBalanceBehaviour.HAS_PANIC;
import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.metering.HandleEnergyBalanceBehaviour.NO_PANIC;

public class CollectEnergyStatusBehaviour extends BaseMessageHandlerBehaviour {
    public static final String ENERGY_USAGE_REQUEST_MSG = "energy-usage-request-msg";
    public static final String PANIC_CFP = "panic-cfp";
    private boolean received = false;
    private boolean panic = false;
    public CollectEnergyStatusBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);
    }

    @Override
    public void onStart() {
        received = false;
        panic = false;
    }

    @Override
    protected void handleCfp(ACLMessage msg) {
        received = true;
        panic = true;
        getDataStore().put(PANIC_CFP, msg);
    }

    @Override
    protected void handleRequest(ACLMessage msg) {
        received = true;
        getDataStore().put(ENERGY_USAGE_REQUEST_MSG, msg);
    }

    @Override
    public boolean done() {
        return received;
    }

    @Override
    public int onEnd() {
        return panic ?
                HAS_PANIC :
                NO_PANIC;
    }
}
