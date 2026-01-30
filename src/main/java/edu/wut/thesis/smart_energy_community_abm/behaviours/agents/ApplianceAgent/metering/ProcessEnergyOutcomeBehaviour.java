package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.metering;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import jade.lang.acl.ACLMessage;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Metering.ALLOWED_GREEN_ENERGY;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Metering.PANIC_CFP;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.TransitionKeys.Metering.HAS_PANIC;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.TransitionKeys.Metering.NO_PANIC;

public final class ProcessEnergyOutcomeBehaviour extends BaseMessageHandlerBehaviour {
    private boolean msgReceived = false;
    private boolean panic = false;

    public ProcessEnergyOutcomeBehaviour(ApplianceAgent agent) {
        super(agent);
    }

    @Override
    public void onStart() {
        msgReceived = false;
        panic = false;
    }

    @Override
    protected void handleCfp(ACLMessage msg) {
        msgReceived = true;
        panic = true;
        getDataStore().put(PANIC_CFP, msg);
    }

    @Override
    protected void handleRequest(ACLMessage msg) {
        msgReceived = true;
        getDataStore().put(ALLOWED_GREEN_ENERGY, msg);
    }

    @Override
    public boolean done() {
        return msgReceived;
    }

    @Override
    public int onEnd() {
        return panic ?
                HAS_PANIC :
                NO_PANIC;
    }
}
