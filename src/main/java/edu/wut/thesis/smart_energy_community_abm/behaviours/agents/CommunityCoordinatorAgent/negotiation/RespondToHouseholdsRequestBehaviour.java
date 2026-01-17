package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.negotiation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.LongFunction;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Negotiation.HOUSEHOLD_RESPONSE;
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

    @Override
    public void action() {
        try {
            // TODO: Check schedule if we can fit the requested allocation - use predictions
            final ObjectMapper mapper = new ObjectMapper();

            final ACLMessage msg = (ACLMessage) getDataStore().get(HOUSEHOLD_RESPONSE);

            final Map<Long, Double> request = mapper.readValue(
                    msg.getContent(),
                    new TypeReference<>() {
                    }
            );
            final long startTick = Collections.min(request.keySet());
            final long endTick = Collections.max(request.keySet());
            final LongFunction<Double> energyPerTick = (tick) -> agent.getAllocatedAt(tick) + request.get(tick);

            final Map<Long, Double> overloadedTicks = agent.calculateAverageProduction(startTick, endTick, energyPerTick);
            agent.log(overloadedTicks.toString(), LogSeverity.DEBUG, this);
            final ACLMessage reply = msg.createReply();

            if (overloadedTicks.values().stream().filter(n -> n > 0).toList().isEmpty()) {
                request.forEach((key, value) -> agent.addAllocation(key, msg.getSender(), value));

                reply.setPerformative(ACLMessage.CONFIRM);
                agent.send(reply);
                return;
            }

            final Map<Long, Double> response = new HashMap<>();
            overloadedTicks.entrySet().stream().filter(e -> e.getValue() > 0).forEach(e -> response.put(e.getKey(), e.getValue()));

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
