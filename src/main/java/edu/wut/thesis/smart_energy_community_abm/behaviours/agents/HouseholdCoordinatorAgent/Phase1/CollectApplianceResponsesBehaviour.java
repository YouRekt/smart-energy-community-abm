package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase1;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.TimeoutMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.lang.acl.ACLMessage;

public final class CollectApplianceResponsesBehaviour extends TimeoutMessageHandlerBehaviour {
    private final HouseholdCoordinatorAgent agent;

    public CollectApplianceResponsesBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent, ApplianceTickRelayBehaviour.HEALTH_REPLY_BY);
        this.agent = agent;
    }

    @Override
    public void onStart() {
        super.onStart();
        setExpectedResponses(agent.applianceCount);
    }

    @Override
    protected void handleConfirm(ACLMessage msg) {
        if (!isMessageTimely(msg)) {
            agent.log("Received a stale message " + ((msg.getContent() == null) ? "" : msg.getContent()), LogSeverity.WARNING, this);
        } else {
            agent.healthyAppliances.add(msg.getSender());
            incrementReceivedCount();
        }
    }
}