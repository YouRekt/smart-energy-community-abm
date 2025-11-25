package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.Phase2;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import jade.lang.acl.ACLMessage;

import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.Phase2.ProcessEnergyOutcomeBehaviour.ALLOWED_GREEN_ENERGY;

public final class ProcessResponseBehaviour extends BaseMessageHandlerBehaviour {
    private ApplianceAgent agent;
    private boolean msgReceived = false;

    public ProcessResponseBehaviour(ApplianceAgent agent) {
        super(agent);
    }

    @Override
    public void onStart() {
        msgReceived = false;
    }

    @Override
    protected void handleInform(ACLMessage msg) {
        msgReceived = true;
        final double allowedGreenEnergy = Double.parseDouble(msg.getContent());
        getDataStore().put(ALLOWED_GREEN_ENERGY, allowedGreenEnergy);
    }

    @Override
    public boolean done() {
        return msgReceived || !agent.insufficientEnergy;
    }
}
