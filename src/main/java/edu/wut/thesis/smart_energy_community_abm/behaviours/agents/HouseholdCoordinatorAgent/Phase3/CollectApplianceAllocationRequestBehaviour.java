package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase3;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.EnergyRequest;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase3.SendAllocationRequestBehaviour.REQUEST_REPLY_BY;
import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase3.SendAllocationRequestBehaviour.REQUEST_REPLY_COUNT;

public class CollectApplianceAllocationRequestBehaviour extends BaseMessageHandlerBehaviour {
    public static final String REQUESTED_ALLOCATIONS = "requested-allocations";

    private final Map<AID, List<EnergyRequest>> requestedAllocations = new HashMap<>();

    public CollectApplianceAllocationRequestBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);
    }

    @Override
    public void onStart() {
        repliesReceived = 0;
        requestedAllocations.clear();
    }

    @Override
    public int onEnd() {
        getDataStore().put(REQUESTED_ALLOCATIONS, requestedAllocations);
        return super.onEnd();
    }

    @Override
    public boolean done() {
        final Integer expectedReplies = (Integer) getDataStore().get(REQUEST_REPLY_COUNT);
        if (expectedReplies == null || expectedReplies == 0) {
            agent.log("Expected replies: " + (expectedReplies == null ? "null" : "0"), LogSeverity.ERROR, this);
            return true;
        }

        Date replyBy = (Date) getDataStore().get(REQUEST_REPLY_BY);
        return replyBy.before(new Date()) || repliesReceived == expectedReplies;
    }

    @Override
    protected void handleInform(ACLMessage msg) {
        repliesReceived++;
        ObjectMapper mapper = new ObjectMapper();
        try {
            requestedAllocations.put(msg.getSender(), mapper.readValue(msg.getContent(), new TypeReference<>() {
            }));
        } catch (JsonProcessingException e) {
            agent.log("JsonProcessingException when trying to receive AllocationEntry from appliance", LogSeverity.ERROR, this);
        }
    }

    @Override
    protected void handleRefuse(ACLMessage msg) {
        repliesReceived++;
    }

    @Override
    protected void performBlock() {
        Date replyBy = (Date) getDataStore().get(REQUEST_REPLY_BY);

        block(replyBy.getTime() - System.currentTimeMillis());
    }
}
