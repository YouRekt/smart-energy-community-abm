package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.lang.acl.ACLMessage;

public final class ProcessBatteryResponseBehaviour extends BaseMessageHandlerBehaviour {
    private boolean receivedMessage = false;

    public ProcessBatteryResponseBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
    }

    @Override
    public void onStart() {
        receivedMessage = false;
    }

    @Override
    protected void handleInform(ACLMessage msg) {
        receivedMessage = true;
        Double externalEnergyUsed = Double.parseDouble(msg.getContent());
        if (externalEnergyUsed > 0.0) {
            // TODO: Implement pulling from external grid
            agent.log("Battery reported a deficit, we have to pull energy from grid", LogSeverity.ERROR, this);
        }
    }

    @Override
    public boolean done() {
        return receivedMessage;
    }


}
