package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.negotiation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.LongFunction;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Negotiation.*;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.TransitionKeys.Negotiation.NOT_OVERLOADED;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.TransitionKeys.Negotiation.OVERLOADED;

public final class RespondToHouseholdsRequestBehaviour extends OneShotBehaviour {
    private final CommunityCoordinatorAgent agent;
    private boolean overloaded = false;

    public RespondToHouseholdsRequestBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void onStart() {
        overloaded = false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void action() {
        try {
            final ObjectMapper mapper = new ObjectMapper();

            final ACLMessage msg = (ACLMessage) getDataStore().get(HOUSEHOLD_RESPONSE);
            final Map<Long, Double> request;
            final AID currentHousehold;
            final ACLMessage reply = new ACLMessage(ACLMessage.INFORM);

            if (msg == null) {
                currentHousehold = ((List<AID>) getDataStore().get(AGENT_LIST)).getFirst();
                request = ((Map<AID, Map<Long, Double>>) getDataStore().get(HOUSEHOLD_REQUESTS_MAP)).get(currentHousehold);
            } else {
                currentHousehold = msg.getSender();
                request = mapper.readValue(
                        msg.getContent(),
                        new TypeReference<>() {
                        }
                );
                getDataStore().put(HOUSEHOLD_RESPONSE, null);
            }

            final long startTick = Collections.min(request.keySet());
            final long endTick = Collections.max(request.keySet());
            final LongFunction<Double> energyPerTick = (tick) -> agent.getAllocatedAt(tick) + request.getOrDefault(tick, 0.0);

            final Map<Long, Double> overloadedTicks = agent.calculateAverageProduction(startTick, endTick, energyPerTick);
            reply.addReceiver(currentHousehold);

            if (overloadedTicks.entrySet().stream()
                    .filter(e -> e.getValue() > 0)
                    .filter(e -> request.containsKey(e.getKey()))
                    .toList()
                    .isEmpty()) {
                request.forEach((key, value) -> agent.addAllocation(key, currentHousehold, value));

                reply.setPerformative(ACLMessage.CONFIRM);
                agent.send(reply);
                return;
            }

            final Map<Long, Double> response = new HashMap<>();
            overloadedTicks.entrySet().stream()
                    .filter(e -> e.getValue() > 0)
                    .filter(e -> request.containsKey(e.getKey()))
                    .forEach(e -> response.put(e.getKey(), e.getValue()));

            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent(mapper.writeValueAsString(response));

            agent.send(reply);
            overloaded = true;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int onEnd() {
        return overloaded ?
                OVERLOADED :
                NOT_OVERLOADED;
    }
}
