package edu.wut.thesis.smart_energy_community_abm.agents;

import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.SimulationTickBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.AllocationEntry;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import edu.wut.thesis.smart_energy_community_abm.domain.PanicContext;
import edu.wut.thesis.smart_energy_community_abm.domain.PriorityContext;
import edu.wut.thesis.smart_energy_community_abm.domain.prediction.EnergyPredictionModel;
import edu.wut.thesis.smart_energy_community_abm.domain.strategy.NegotiationStrategy;
import jade.core.AID;

import java.util.*;
import java.util.function.LongFunction;

public final class CommunityCoordinatorAgent extends BaseAgent {
    public final List<AID> householdAgents = new ArrayList<>();
    public final List<AID> energyAgents = new ArrayList<>();
    public final Map<AID, Double> greenScores = new HashMap<>();
    public final Map<AID, Double> cooperationScores = new HashMap<>();
    public final TreeMap<Long, Map<AID, Double>> allocations = new TreeMap<>();
    public AID batteryAgent;
    public Double minChargeThreshold = 0.2;
    public long tick = 0;
    public short phase = 1;
    public Integer householdCount;
    public Integer energySourceCount;
    public double runningAvgProduction = 0.0;
    public long productionSampleCount = 0;
    public NegotiationStrategy strategy;

    private EnergyPredictionModel predictionModel;

    @Override
    protected void setup() {
        super.setup();

        final Object[] args = getArguments();

        householdCount = (Integer) args[0];

        if (householdCount == null) {
            log("Household agent count is missing", LogSeverity.ERROR, this);
            throw new RuntimeException("Household agent count is missing");
        }

        energySourceCount = (Integer) args[1];

        if (energySourceCount == null) {
            log("Energy Agent count is missing", LogSeverity.ERROR, this);
            throw new RuntimeException("Energy Agent count is missing");
        }

        strategy = (NegotiationStrategy) args[2];

        log("Using Negotiation Strategy: " + strategy.getName(), LogSeverity.INFO, this);

        predictionModel = (EnergyPredictionModel) args[3];

        log("Using Prediction Model: " + predictionModel.getName(), LogSeverity.INFO, this);

        addBehaviour(new SimulationTickBehaviour(this));
    }

    public double computePriority(AID aid, AllocationEntry entry, long currentTick) {
        double greenScore = greenScores.getOrDefault(aid, 0.5);
        double cooperationScore = cooperationScores.getOrDefault(aid, 0.5);
        double totalEnergy = entry.requestedEnergy();

        PriorityContext ctx = new PriorityContext(entry, currentTick, greenScore, cooperationScore, totalEnergy);
        return strategy.computePriority(ctx);
    }

    public boolean shouldTriggerPanic(double shortfall, double batteryCharge, int householdsAffected) {
        PanicContext ctx = new PanicContext(shortfall, batteryCharge, minChargeThreshold, householdsAffected);
        return strategy.shouldTriggerPanic(ctx);
    }

    public double getAllocatedAt(long tick) {
        return allocations
                .getOrDefault(tick, Map.of())
                .values()
                .stream()
                .reduce(0.0, Double::sum);
    }

    public double getAllocatedAtFor(long tick, AID aid) {
        return allocations
                .getOrDefault(tick, Map.of())
                .getOrDefault(aid, 0.0);
    }

    public void updateRunningAverage(double production) {
        runningAvgProduction += (production - runningAvgProduction) / ++productionSampleCount;
    }

    public void addAllocation(long tick, AID householdId, Double amount) {
        allocations
                .computeIfAbsent(tick, _ -> new HashMap<>())
                .merge(householdId, amount, Double::sum);
    }

    public void removeAllocation(long tick, AID householdId) {
        allocations.computeIfAbsent(tick, _ -> new HashMap<>()).remove(householdId);

        if (allocations.get(tick).isEmpty())
            allocations.remove(tick);
    }

    public void updateCooperationScore(AID householdId, boolean accepted) {
        double current = cooperationScores.getOrDefault(householdId, 0.5);
        double updated = accepted
                ? Math.min(1.0, current + 0.05)
                : Math.max(0.0, current - 0.03);
        cooperationScores.put(householdId, updated);
    }

    public double getPredictedMaxAmount(long tick) {
        double predictedMax = predictionModel.predictAvailableEnergy(tick);
        log("Predicting " + predictedMax + " energy for tick " + tick, LogSeverity.DEBUG, this);
        return predictedMax;
    }

    public void updatePredictionModel(double production, double batteryCharge) {
        predictionModel.update(production, batteryCharge);
    }

    public Map<Long, Double> calculateAverageProduction(long startTick, long endTick, LongFunction<Double> loadPerTickProvider) {
        return predictionModel.simulateEnergyBalances(startTick, endTick, loadPerTickProvider);
    }
}
