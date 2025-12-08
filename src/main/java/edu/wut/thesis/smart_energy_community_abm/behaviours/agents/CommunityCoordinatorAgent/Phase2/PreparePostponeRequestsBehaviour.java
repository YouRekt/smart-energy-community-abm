package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.AllocationEntry;
import edu.wut.thesis.smart_energy_community_abm.domain.EnergyRequest;
import edu.wut.thesis.smart_energy_community_abm.domain.PostponeRequest;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class PreparePostponeRequestsBehaviour extends OneShotBehaviour {
    public static final String POSTPONE_REQUESTS = "postpone-requests";
    private final CommunityCoordinatorAgent agent;

    public PreparePostponeRequestsBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void action() {
        Map<AID, AllocationEntry> tickAllocations = agent.allocations.getOrDefault(agent.tick, Map.of());

        List<PostponeRequest> requests = tickAllocations.values().stream()
                .map(entry -> new PostponeRequest(
                        entry.requesterId(),
                        agent.tick,
                        entry.grantedEnergy(),
                        agent.computePriority(toEnergyRequest(entry), agent.tick)
                ))
                .sorted(Comparator.comparingDouble(PostponeRequest::priority))
                .toList();

        getDataStore().put(POSTPONE_REQUESTS, requests);
    }

    private EnergyRequest toEnergyRequest(AllocationEntry entry) {
        return new EnergyRequest(
                entry.requesterId(),
                agent.tick,
                1,  // duration not relevant for priority calc
                entry.requestedEnergy(),
                true,
                entry.requestTimestamp()
        );
    }
}
