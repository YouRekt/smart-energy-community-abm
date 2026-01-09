package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.metering.Panic;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.TimeoutMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

public class CollectPostponeRepliesBehaviour extends TimeoutMessageHandlerBehaviour {
    private final List<AID> postponeAgreements = new ArrayList<>();

    public CollectPostponeRepliesBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent, DataStoreKey.Metering.Panic.POSTPONE_REPLY_BY);
    }

    @Override
    public void onStart() {
        super.onStart();
        postponeAgreements.clear();

        Integer expected = (Integer) getDataStore().get(DataStoreKey.Metering.Panic.POSTPONE_REPLY_COUNT);
        if (expected != null) {
            setExpectedResponses(expected);
        } else {
            agent.log("Expected replies count is null", LogSeverity.ERROR, this);
        }
    }

    @Override
    public int onEnd() {
        getDataStore().put(DataStoreKey.Metering.Panic.POSTPONE_AGREEMENTS, postponeAgreements);
        return super.onEnd();
    }

    @Override
    protected void handlePropose(ACLMessage msg) {
        if (!isMessageTimely(msg)) {
            agent.log("Received a stale message " + ((msg.getContent() == null) ? "" : msg.getContent()), LogSeverity.WARNING, this);
        } else {
            postponeAgreements.add(msg.getSender());
            incrementReceivedCount();
        }
    }

    @Override
    protected void handleRefuse(ACLMessage msg) {
        if (!isMessageTimely(msg)) {
            agent.log("Received a stale message " + ((msg.getContent() == null) ? "" : msg.getContent()), LogSeverity.WARNING, this);
        } else {
            incrementReceivedCount();
        }
    }
}
