package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.negotiation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.List;
import java.util.Map;

import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.negotiation.RequestAllocationReservationsBehaviour.AGENT_LIST;
import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.negotiation.RequestAllocationReservationsBehaviour.OVERLOADED_TICKS;

public class SendRequestToHouseholdBehaviour extends OneShotBehaviour {
    private final CommunityCoordinatorAgent agent;

    public SendRequestToHouseholdBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void action() {
        final ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        final AID firstHousehold = ((List<AID>) getDataStore().get(AGENT_LIST)).getFirst();

        request.addReceiver(firstHousehold);

        final Map<Long, Double> overloadedTicks = (Map<Long, Double>) getDataStore().get(OVERLOADED_TICKS);
        final ObjectMapper mapper = new ObjectMapper();

        if (overloadedTicks == null) {
            request.setContent(null);
        }
        else {
            try {
                request.setContent(mapper.writeValueAsString(overloadedTicks));
            } catch (JsonProcessingException e) {
                agent.log("JsonProcessingException when setting overloadedTicks", LogSeverity.ERROR, this);
                request.setContent(null);
            }
        }
        agent.send(request);
    }
}
