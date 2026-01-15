package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.negotiation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.HashMap;
import java.util.Map;

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

            final Map<Long, Double> overloadedTicks = new HashMap<>();

            request.keySet().forEach(t -> {
                final double allocatedAmount = agent.getAllocatedAt(t);
                final double predictedMaxAmount = agent.getPredictedMaxAmount(t);

                final double overloadedAmount = (request.get(t) + allocatedAmount) - predictedMaxAmount;

                if (overloadedAmount > 0)
                    overloadedTicks.put(t, overloadedAmount);
            });

            final ACLMessage reply = msg.createReply();

            if (overloadedTicks.isEmpty()) {

                request.forEach((key, value) -> agent.addAllocation(key, msg.getSender(), value));

                reply.setPerformative(ACLMessage.CONFIRM);
                agent.send(reply);
                return;
            }

            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent(mapper.writeValueAsString(overloadedTicks));

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
