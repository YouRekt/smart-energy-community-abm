package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase3;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.EnergyRequest;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.HashMap;
import java.util.Map;

import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase3.CollectApplianceAllocationRequestBehaviour.REQUESTED_ALLOCATIONS;
import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase3.CollectCommunityAllocationRequestBehaviour.ALLOCATION_REQUEST;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REFUSE;

public class CalculateTimetableAllocationsBehaviour extends OneShotBehaviour {
    private final HouseholdCoordinatorAgent agent;
    private boolean refused = false;

    public CalculateTimetableAllocationsBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void onStart() {
        refused = false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void action() {
        try {
            final Map<AID, EnergyRequest> requestedAllocations = (Map<AID, EnergyRequest>) getDataStore().get(REQUESTED_ALLOCATIONS);

            final ACLMessage response = (ACLMessage) getDataStore().get(ALLOCATION_REQUEST);

            if (requestedAllocations.isEmpty()) {
                response.setPerformative(REFUSE);
                agent.send(response);
                refused = true;
                return;
            }

            final ObjectMapper mapper = new ObjectMapper();

            final Map<Long, Double> allocationByTick = new HashMap<>();

            if(response.getContent() != null){
                // TODO: Handle removing some tasks based on the received map
            }

            requestedAllocations.values().forEach(energyRequest -> {
                for (long t = energyRequest.startTick(); t <= energyRequest.startTick() + energyRequest.duration() - 1; t++) {
                    allocationByTick.merge(t, energyRequest.energyPerTick(), Double::sum);
                }
            });

            response.setPerformative(INFORM);
            response.setContent(mapper.writeValueAsString(allocationByTick));

            agent.send(response);
        } catch (JsonProcessingException e) {
            agent.log("JsonProcessingException when trying to write allocationByTick", LogSeverity.ERROR, this);
        }
    }

    @Override
    public int onEnd() {
        return refused ?
                REFUSE :
                INFORM;
    }
}
