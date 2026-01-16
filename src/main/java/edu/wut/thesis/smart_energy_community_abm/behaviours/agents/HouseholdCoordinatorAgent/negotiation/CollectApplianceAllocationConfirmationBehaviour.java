package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.negotiation;

import edu.wut.thesis.smart_energy_community_abm.agents.BaseAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.TimeoutMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.lang.acl.ACLMessage;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Negotiation.ALLOCATION_REQUEST;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Negotiation.APPLIANCE_CONFIRM_MESSAGES;

// TODO: Remove any logic besides receiving and saving data from messages - applies on all messagehandlers
public final class CollectApplianceAllocationConfirmationBehaviour extends TimeoutMessageHandlerBehaviour {
    public CollectApplianceAllocationConfirmationBehaviour(BaseAgent agent, String deadlineDataStoreKey) {
        super(agent, deadlineDataStoreKey);
    }

    @Override
    public void onStart() {
        super.onStart();
        Integer expected = (Integer) getDataStore().get(APPLIANCE_CONFIRM_MESSAGES);
        if (expected != null) {
            setExpectedResponses(expected);
        } else {
            agent.log("Expected replies count is null", LogSeverity.ERROR, this);
        }
    }

    @Override
    public int onEnd() {
        final ACLMessage request = (ACLMessage) getDataStore().get(ALLOCATION_REQUEST);
        final ACLMessage communityCoordinatorReply = request.createReply(ACLMessage.INFORM);
        agent.send(communityCoordinatorReply);
        return super.onEnd();
    }

    @Override
    protected void handleInform(ACLMessage msg) {
        incrementReceivedCount();
    }
}
