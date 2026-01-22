package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.negotiation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.TimeoutMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Negotiation.*;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.TransitionKeys.Negotiation.FINISHED;
import static jade.lang.acl.ACLMessage.INFORM;

public final class CollectInitialHouseholdResponsesBehaviour extends TimeoutMessageHandlerBehaviour {
    private final CommunityCoordinatorAgent agent;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<AID, Map<Long, Double>> householdRequests = new HashMap<>() {
    };

    public CollectInitialHouseholdResponsesBehaviour(CommunityCoordinatorAgent agent) {
        super(agent, REQUEST_REPLY_BY);
        this.agent = agent;
    }

    @Override
    public void onStart() {
        super.onStart();
        householdRequests.clear();

        Integer expected = (Integer) getDataStore().get(REQUEST_AMOUNT);
        if (expected != null && expected > 0) {
            setExpectedResponses(expected);
        } else {
            agent.log("Expected replies count is null", LogSeverity.ERROR, this);
        }
    }

    @Override
    protected void handleRefuse(ACLMessage msg) {
        incrementReceivedCount();
        agent.log("Refusal by household " + msg.getSender().getLocalName() + " acknowledged", LogSeverity.DEBUG, this);
        getDataStore().put(REQUEST_AMOUNT, (Integer) getDataStore().get(REQUEST_AMOUNT) - 1);
    }

    @Override
    protected void handleInform(ACLMessage msg) {
        incrementReceivedCount();
        try {
            householdRequests.put(msg.getSender(), mapper.readValue(msg.getContent(), new TypeReference<>() {
            }));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int onEnd() {
        if (householdRequests.isEmpty()) {
            return FINISHED;
        } else {
            getDataStore().put(HOUSEHOLD_REQUESTS_MAP, householdRequests);
            getDataStore().put(AGENT_LIST, new ArrayList<>(householdRequests.entrySet().stream()
                    .sorted((e, f) -> agent.computeNegotiationPriority(f.getKey(), f.getValue()).compareTo(agent.computeNegotiationPriority(e.getKey(), e.getValue())))
                    .map(Map.Entry::getKey)
                    .toList()));
            return INFORM;
        }
    }
}
