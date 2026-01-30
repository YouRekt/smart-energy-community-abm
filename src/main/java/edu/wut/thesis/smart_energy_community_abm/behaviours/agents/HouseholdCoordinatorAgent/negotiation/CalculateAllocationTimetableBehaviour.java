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

import static edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent.MAX_NEGOTIATION_RETRIES;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Negotiation.*;
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
            int tries = (Integer) getDataStore().get(NEGOTIATION_RETRIES);

            final Map<AID, List<EnergyRequest>> requestedAllocations = (Map<AID, List<EnergyRequest>>) getDataStore().get(REQUESTED_ALLOCATIONS);
            final ACLMessage communityResponse = (ACLMessage) getDataStore().get(ALLOCATION_REQUEST);
            final ACLMessage response = communityResponse.createReply();

            if (requestedAllocations == null || requestedAllocations.isEmpty() || tries > MAX_NEGOTIATION_RETRIES) {
                if (requestedAllocations == null || requestedAllocations.isEmpty())
                    agent.log("Received 0 allocation requests or none fit the Community schedule", LogSeverity.INFO, this);
                if (tries > MAX_NEGOTIATION_RETRIES)
                    agent.log("Exceeded max request allocation retries, quitting", LogSeverity.WARN, this);
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

                List<Long> sortedOverloadTicks = new ArrayList<>(rawOverloads.keySet());
                Collections.sort(sortedOverloadTicks);

                Map<Long, Double> currentOverloads = new HashMap<>(rawOverloads);

                for (Long tick : sortedOverloadTicks) {
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

                    if (candidates.isEmpty())
                        continue;

                    double maxCapacity = totalRequestedAtTick - overloadAmount;
                    Set<EnergyRequest> keptRequests = solveWeightedKnapsack(candidates, maxCapacity);

                    for (EnergyRequest req : candidates) {
                        if (!keptRequests.contains(req)) {
                            // 1. Remove from global pool
                            allRequests.remove(req);

                            // 2. Credit energy back to future ticks
                            long end = req.startTick() + req.duration();
                            for (long t = req.startTick(); t < end; t++) {
                                currentOverloads.computeIfPresent(t, (_, v) -> v - req.energyPerTick());
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
                tries++;
                getDataStore().put(NEGOTIATION_RETRIES, tries);

                agent.send(response);
            }
        } catch (JsonProcessingException e) {
            agent.log("JsonProcessingException when trying to write allocationByTick", LogSeverity.ERROR, this);
        }
    }

    private Set<EnergyRequest> solveWeightedKnapsack(List<EnergyRequest> items, double limit) {
        if (limit <= 0.0001) {
            return Collections.emptySet();
        }
        if (items.isEmpty()) {
            return Collections.emptySet();
        }

        final double SCALE = 1000.0;
        // Bonus per tick of duration to break ties in favor of longer tasks
        final double DURATION_BONUS = 0.1;

        int capacity = (int) Math.floor(limit * SCALE);
        int n = items.size();

        int[] weights = new int[n];
        double[] values = new double[n];

        for (int i = 0; i < n; i++) {
            EnergyRequest req = items.get(i);
            weights[i] = (int) Math.round(req.energyPerTick() * SCALE);

            values[i] = req.energyPerTick() + (req.duration() * DURATION_BONUS);
        }

        // dp[w] = max value achievable at weight w
        double[] dp = new double[capacity + 1];
        // keep[i][w] = did we include item i to get weight w?
        boolean[][] keep = new boolean[n][capacity + 1];

        for (int i = 0; i < n; i++) {
            int w_i = weights[i];
            double v_i = values[i];

            for (int w = capacity; w >= w_i; w--) {
                double newValue = dp[w - w_i] + v_i;
                if (newValue > dp[w]) {
                    dp[w] = newValue;
                    keep[i][w] = true;
                }
            }
        }

        Set<EnergyRequest> keptItems = new HashSet<>();

        double maxVal = -1;
        int currentW = 0;
        for (int w = 0; w <= capacity; w++) {
            if (dp[w] > maxVal) {
                maxVal = dp[w];
                currentW = w;
            }
        }

        for (int i = n - 1; i >= 0; i--) {
            if (keep[i][currentW]) {
                keptItems.add(items.get(i));
                currentW -= weights[i];
            }
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
