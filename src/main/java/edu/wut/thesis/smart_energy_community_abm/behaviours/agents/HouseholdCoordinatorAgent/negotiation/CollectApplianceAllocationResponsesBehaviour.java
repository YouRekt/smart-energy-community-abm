package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.negotiation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.TimeoutMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.EnergyRequest;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Negotiation.REQUEST_REPLY_BY;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Negotiation.REQUEST_REPLY_COUNT;

public final class CollectApplianceAllocationResponsesBehaviour extends TimeoutMessageHandlerBehaviour {
    private final Map<AID, List<EnergyRequest>> requestedAllocations = new HashMap<>();

    public CollectApplianceAllocationResponsesBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent, REQUEST_REPLY_BY);
    }

    @Override
    public void onStart() {
        super.onStart();
        requestedAllocations.clear();

        Integer expected = (Integer) getDataStore().get(REQUEST_REPLY_COUNT);
        if (expected != null) {
            setExpectedResponses(expected);
        } else {
            agent.log("Expected replies count is null", LogSeverity.ERROR, this);
        }
    }

    @Override
    public int onEnd() {
        getDataStore().put(DataStoreKey.Negotiation.REQUESTED_ALLOCATIONS, requestedAllocations);
        return super.onEnd();
    }

    @Override
    protected void handleInform(ACLMessage msg) {
        if (!isMessageTimely(msg)) {
            agent.log("Received a stale message " + ((msg.getContent() == null) ? "" : msg.getContent()), LogSeverity.WARN, this);
        } else {
            try {
                ObjectMapper mapper = new ObjectMapper();
                final List<EnergyRequest> energyRequest = mapper.readValue(msg.getContent(), new TypeReference<>() {});
                requestedAllocations.put(msg.getSender(), energyRequest);
                incrementReceivedCount();
            } catch (JsonProcessingException e) {
                agent.log("JsonProcessingException when trying to receive EnergyRequest from appliance: " + e.getMessage(), LogSeverity.ERROR, this);
            }
        }
    }

    @Override
    protected void handleRefuse(ACLMessage msg) {
        if (!isMessageTimely(msg)) {
            agent.log("Received a stale message " + ((msg.getContent() == null) ? "" : msg.getContent()), LogSeverity.WARN, this);
        } else {
            incrementReceivedCount();
        }
    }
}
