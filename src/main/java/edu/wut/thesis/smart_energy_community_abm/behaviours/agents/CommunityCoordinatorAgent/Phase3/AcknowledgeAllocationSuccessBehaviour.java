package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase3;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.lang.acl.ACLMessage;

public class AcknowledgeAllocationSuccessBehaviour extends BaseMessageHandlerBehaviour {
    private boolean receivedMsg = false;

    public AcknowledgeAllocationSuccessBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
    }

    @Override
    public void onStart() {
        receivedMsg = false;
    }

    @Override
    public boolean done() {
        return receivedMsg;
    }

    @Override
    protected void handleInform(ACLMessage msg) {
        receivedMsg = true;
        agent.log("Allocation by household " + msg.getSender().getLocalName() + " acknowledged", LogSeverity.DEBUG, this);
    }
}
