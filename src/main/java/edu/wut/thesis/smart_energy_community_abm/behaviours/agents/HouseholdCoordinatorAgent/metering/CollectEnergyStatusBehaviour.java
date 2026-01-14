package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.metering;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey;
import jade.lang.acl.ACLMessage;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.TransitionKeys.Metering.HAS_PANIC;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.TransitionKeys.Metering.NO_PANIC;

public final class CollectEnergyStatusBehaviour extends BaseMessageHandlerBehaviour {
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
        getDataStore().put(DataStoreKey.Metering.PANIC_CFP, msg);
    }

    @Override
    protected void handleRequest(ACLMessage msg) {
        received = true;
        getDataStore().put(DataStoreKey.Metering.ENERGY_USAGE_REQUEST_MSG, msg);
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
