package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase3;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.AllocationEntry;
import edu.wut.thesis.smart_energy_community_abm.domain.EnergyRequest;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase3.CollectApplianceAllocationResponsesBehaviour.REQUESTED_ALLOCATIONS;
import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase3.CollectAllocationNegotiationStartRequestBehaviour.ALLOCATION_REQUEST;

public class FinalizeAllocationNegotiationBehaviour extends OneShotBehaviour {
    private final HouseholdCoordinatorAgent agent;

    public FinalizeAllocationNegotiationBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void action() {
        final ACLMessage request = (ACLMessage) getDataStore().get(ALLOCATION_REQUEST);
        final Map<AID, List<EnergyRequest>> requestedAllocations = (Map<AID, List<EnergyRequest>>) getDataStore().get(REQUESTED_ALLOCATIONS);

        requestedAllocations.forEach((aid, list) -> list.forEach((e) -> {
            for (long t = e.startTick(); t <= e.endTick(); t++) {
                agent.timetable.computeIfAbsent(t, _ -> new HashMap<>()).put(aid, new AllocationEntry(
                        e.energyPerTick(),
                        agent.tick,
                        e.startTick(),
                        e.duration()
                ));
            }
        }));

        final ACLMessage communityCoordinatorReply = request.createReply(ACLMessage.INFORM);
        agent.send(communityCoordinatorReply);

        final ACLMessage applianceReply = new ACLMessage(ACLMessage.CONFIRM);
        requestedAllocations.keySet().forEach(applianceReply::addReceiver);
        agent.send(applianceReply);
    }
}
