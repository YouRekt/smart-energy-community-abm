package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.negotiation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.EnergyRequest;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.*;
import java.util.stream.Collectors;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Negotiation.REQUESTED_ALLOCATIONS;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Negotiation.ALLOCATION_REQUEST;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REFUSE;

public final class CalculateAllocationTimetableBehaviour extends OneShotBehaviour {
    private final HouseholdCoordinatorAgent agent;
    private boolean refused = false;

    public CalculateAllocationTimetableBehaviour(HouseholdCoordinatorAgent agent) {
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
            final Map<AID, List<EnergyRequest>> requestedAllocations = (Map<AID, List<EnergyRequest>>) getDataStore().get(REQUESTED_ALLOCATIONS);

            final ACLMessage communityResponse = (ACLMessage) getDataStore().get(ALLOCATION_REQUEST);
            final ACLMessage response = communityResponse.createReply();

            if (requestedAllocations == null || requestedAllocations.isEmpty()) {
                agent.log("Received 0 allocation requests or none fit the Community schedule", LogSeverity.INFO, this);
                response.setPerformative(REFUSE);
                response.setContent("No allocation requests");
                agent.send(response);
                refused = true;
                return;
            }

            final ObjectMapper mapper = new ObjectMapper();

            Set<EnergyRequest> allRequests = requestedAllocations.values().stream()
                    .filter(Objects::nonNull)
                    .flatMap(List::stream)
                    .collect(Collectors.toSet());

            if (communityResponse.getContent() != null && !communityResponse.getContent().isEmpty()) {

                final Map<Long, Double> rawOverloads = mapper.readValue(
                        communityResponse.getContent(),
                        new TypeReference<>() {
                        }
                );

                List<Long> sortedTicks = new ArrayList<>(rawOverloads.keySet());
                Collections.sort(sortedTicks);

                Map<Long, Double> currentOverloads = new HashMap<>(rawOverloads);

                for (Long tick : sortedTicks) {
                    double overloadAmount = currentOverloads.get(tick);

                    if (overloadAmount <= 0) continue;

                    List<EnergyRequest> candidates = new ArrayList<>();
                    double totalRequestedAtTick = 0;

                    for (EnergyRequest req : allRequests) {
                        if (req.isActive(tick)) {
                            candidates.add(req);
                            totalRequestedAtTick += req.energyPerTick();
                        }
                    }

                    double maxCapacity = totalRequestedAtTick - overloadAmount;
                    Set<EnergyRequest> keptRequests = solveSubsetSum(candidates, maxCapacity);

                    for (EnergyRequest req : candidates) {
                        if (!keptRequests.contains(req)) {
                            // 1. Remove from global pool
                            allRequests.remove(req);

                            // 2. Credit energy back to future ticks
                            long end = req.startTick() + req.duration();
                            for (long t = req.startTick(); t < end; t++) {
                                currentOverloads.computeIfPresent(t, (k, v) -> v - req.energyPerTick());
                            }
                        }
                    }
                }

                Map<AID, List<EnergyRequest>> filteredMap = allRequests.stream()
                        .collect(Collectors.groupingBy(EnergyRequest::applianceAID));

                for (var key : requestedAllocations.keySet()) {
                    if (!filteredMap.containsKey(key)) {
                        final ACLMessage applianceRefuse = new ACLMessage(REFUSE);
                        applianceRefuse.addReceiver(key);
                        applianceRefuse.setContent("No requested tasks accepted");
                        agent.send(applianceRefuse);
                    }
                }

                getDataStore().put(REQUESTED_ALLOCATIONS, filteredMap);
            }

            final Map<Long, Double> allocationByTick = new HashMap<>();

            for (EnergyRequest req : allRequests) {
                for (long t = req.startTick(); t < req.startTick() + req.duration(); t++) {
                    allocationByTick.merge(t, req.energyPerTick(), Double::sum);
                }
            }

            if (allRequests.isEmpty()) {
                agent.log("After solveSubsetSum there were no requests left, sending refuse", LogSeverity.WARN, this);
                response.setPerformative(REFUSE);
                response.setContent("No allocation requests after knapsack");
                agent.send(response);
                refused = true;
            } else {
                response.setPerformative(INFORM);
                response.setContent(mapper.writeValueAsString(allocationByTick));

                agent.send(response);
            }
        } catch (JsonProcessingException e) {
            agent.log("JsonProcessingException when trying to write allocationByTick", LogSeverity.ERROR, this);
        }
    }

    private Set<EnergyRequest> solveSubsetSum(List<EnergyRequest> items, double limit) {
        if (limit <= 0.0001) {
            return Collections.emptySet();
        }

        if (items.isEmpty()) {
            return Collections.emptySet();
        }

        final double SCALE = 1000.0;
        int capacity = (int) Math.floor(limit * SCALE);
        int n = items.size();

        int[] weights = new int[n];
        for (int i = 0; i < n; i++) {
            weights[i] = (int) Math.ceil(items.get(i).energyPerTick() * SCALE);
        }

        int[] dp = new int[capacity + 1];
        Arrays.fill(dp, -1);
        dp[0] = -2;

        for (int i = 0; i < n; i++) {
            int weight = weights[i];
            for (int w = capacity; w >= weight; w--) {
                if (dp[w - weight] != -1 && dp[w] == -1) {
                    dp[w] = i;
                }
            }
        }

        int bestWeight = capacity;
        while (bestWeight >= 0 && dp[bestWeight] == -1) {
            bestWeight--;
        }

        Set<EnergyRequest> keptItems = new HashSet<>();
        int currentW = bestWeight;
        while (currentW > 0) {
            int idx = dp[currentW];
            if (idx < 0) break;
            keptItems.add(items.get(idx));
            currentW -= weights[idx];
        }

        return keptItems;
    }

    @Override
    public int onEnd() {
        return refused ?
                REFUSE :
                INFORM;
    }
}
