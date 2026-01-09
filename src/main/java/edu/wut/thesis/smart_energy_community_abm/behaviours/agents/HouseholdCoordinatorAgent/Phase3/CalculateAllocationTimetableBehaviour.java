package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase3;

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

import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase3.CollectApplianceAllocationResponsesBehaviour.REQUESTED_ALLOCATIONS;
import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase3.CollectAllocationNegotiationStartRequestBehaviour.ALLOCATION_REQUEST;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REFUSE;

public class CalculateAllocationTimetableBehaviour extends OneShotBehaviour {
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

            final ACLMessage response = (ACLMessage) getDataStore().get(ALLOCATION_REQUEST);

            if (requestedAllocations == null || requestedAllocations.isEmpty()) {
                response.setPerformative(REFUSE);
                agent.send(response);
                refused = true;
                return;
            }

            final ObjectMapper mapper = new ObjectMapper();

            if (response.getContent() != null && !response.getContent().isEmpty()) {

                final Map<Long, Double> rawOverloads = mapper.readValue(
                        response.getContent(),
                        new TypeReference<>() {
                        }
                );

                List<EnergyRequest> allActiveRequests = requestedAllocations.values().stream().filter(Objects::nonNull)
                        .flatMap(Collection::stream).toList();

                Set<EnergyRequest> globalSurvivors = Collections.newSetFromMap(new IdentityHashMap<>());
                globalSurvivors.addAll(allActiveRequests);

                List<Long> sortedTicks = new ArrayList<>(rawOverloads.keySet());
                Collections.sort(sortedTicks);

                Map<Long, Double> currentOverloads = new HashMap<>(rawOverloads);

                for (Long tick : sortedTicks) {
                    double overloadAmount = currentOverloads.get(tick);

                    if (overloadAmount <= 0) continue;

                    List<EnergyRequest> candidates = new ArrayList<>();
                    double totalRequestedAtTick = 0;

                    for (EnergyRequest req : globalSurvivors) {
                        if (req.isActive(tick)) {
                            candidates.add(req);
                            totalRequestedAtTick += req.energyPerTick();
                        }
                    }

                    double maxCapacity = totalRequestedAtTick - overloadAmount;

                    Set<EnergyRequest> keptRequests = solveSubsetSum(candidates, maxCapacity);

                    for (EnergyRequest req : candidates) {
                        if (keptRequests.contains(req)) {
                            continue;
                        }
                        globalSurvivors.remove(req);

                        long endTick = req.startTick() + req.duration();
                        for (long t = req.startTick(); t < endTick; t++)
                            if (currentOverloads.containsKey(t))
                                currentOverloads.put(t, currentOverloads.get(t) - req.energyPerTick());
                    }
                }

                requestedAllocations.values().stream().filter(Objects::nonNull).forEach(agentList ->
                        agentList.removeIf(req -> !globalSurvivors.contains(req))
                );

                getDataStore().put(REQUESTED_ALLOCATIONS, requestedAllocations);
            }

            final Map<Long, Double> allocationByTick = new HashMap<>();

            for (List<EnergyRequest> agentList : requestedAllocations.values()) {
                if (agentList != null)
                    for (EnergyRequest req : agentList)
                        for (long t = req.startTick(); t < req.startTick() + req.duration(); t++)
                            allocationByTick.merge(t, req.energyPerTick(), Double::sum);
            }

            response.setPerformative(INFORM);
            response.setContent(mapper.writeValueAsString(allocationByTick));

            agent.send(response);
        } catch (JsonProcessingException e) {
            agent.log("JsonProcessingException when trying to write allocationByTick", LogSeverity.ERROR, this);
        }
    }

    private Set<EnergyRequest> solveSubsetSum(List<EnergyRequest> items, double limit) {
        final double SCALE = 1000.0;
        int capacity = (int) Math.floor(limit * SCALE);
        int n = items.size();

        int[] weights = new int[n];
        for (int i = 0; i < n; i++) {
            weights[i] = (int) Math.ceil(items.get(i).energyPerTick() * SCALE);
        }

        int[] dp = new int[capacity + 1];
        java.util.Arrays.fill(dp, -1);
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

        Set<EnergyRequest> keptItems = Collections.newSetFromMap(new IdentityHashMap<>());
        int currentW = bestWeight;

        while (currentW > 0) {
            int itemIndex = dp[currentW];
            if (itemIndex < 0) break;

            keptItems.add(items.get(itemIndex));
            currentW -= weights[itemIndex];
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
