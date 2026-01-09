package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase3;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.TimeoutMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.EnergyRequest;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectApplianceAllocationResponsesBehaviour extends TimeoutMessageHandlerBehaviour {
    public static final String REQUESTED_ALLOCATIONS = "requested-allocations";

    private final Map<AID, List<EnergyRequest>> requestedAllocations = new HashMap<>();

    public CollectApplianceAllocationResponsesBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent, SendApplianceAllocationRequestBehaviour.REQUEST_REPLY_BY);
    }

    @Override
    public void onStart() {
        super.onStart();
        requestedAllocations.clear();

        Integer expected = (Integer) getDataStore().get(SendApplianceAllocationRequestBehaviour.REQUEST_REPLY_COUNT);
        if (expected != null) {
            setExpectedResponses(expected);
        } else {
            agent.log("Expected replies count is null", LogSeverity.ERROR, this);
        }
    }

    @Override
    public int onEnd() {
        getDataStore().put(REQUESTED_ALLOCATIONS, requestedAllocations);
        return super.onEnd();
    }

    @Override
    protected void handleInform(ACLMessage msg) {
        if (!isMessageTimely(msg)) {
            agent.log("Received a stale message " + ((msg.getContent() == null) ? "" : msg.getContent()), LogSeverity.WARNING, this);
        } else {
            ObjectMapper mapper = new ObjectMapper();
            try {
                requestedAllocations.put(msg.getSender(), mapper.readValue(msg.getContent(), new TypeReference<>() {
                }));
                incrementReceivedCount();
            } catch (JsonProcessingException e) {
                agent.log("JsonProcessingException when trying to receive AllocationEntry from appliance", LogSeverity.ERROR, this);
            }
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
