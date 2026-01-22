package edu.wut.thesis.smart_energy_community_abm.agents;

import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.SimulationTickBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.PanicContext;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import edu.wut.thesis.smart_energy_community_abm.domain.prediction.EnergyPredictionModel;
import edu.wut.thesis.smart_energy_community_abm.domain.prediction.MovingAveragePredictionModel;
import edu.wut.thesis.smart_energy_community_abm.domain.strategy.NegotiationStrategy;
import jade.core.AID;

import java.util.*;
import java.util.function.LongFunction;

// TODO: Perhaps add more robust logging, that will show when agents are postponing their tasks, negotiating etc.? Just for more data
public final class CommunityCoordinatorAgent extends BaseAgent {
    public static final int MAX_NEGOTIATION_RETRIES = 5;
    public final List<AID> householdAgents = new ArrayList<>();
    public final List<AID> energyAgents = new ArrayList<>();

    // Scores are normalized 0.0 to 1.0
    public final Map<AID, Double> greenScores = new HashMap<>();
    public final Map<AID, Double> cooperationScores = new HashMap<>();

    public final TreeMap<Long, Map<AID, Double>> allocations = new TreeMap<>();
    public AID batteryAgent;
    public Double minChargeThreshold = 0.2;
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

    public Double computeNegotiationPriority(AID aid, Map<Long, Double> requestMap) {
        if (requestMap == null || requestMap.isEmpty()) {
            return 0.0;
        }

        double greenScore = greenScores.getOrDefault(aid, 0.0);
        double cooperationScore = cooperationScores.getOrDefault(aid, 0.5);

        long firstTaskTick = Long.MAX_VALUE;
        long lastTaskTick = Long.MIN_VALUE;

        for (Long tick : requestMap.keySet()) {
            if (tick < firstTaskTick) firstTaskTick = tick;
            if (tick > lastTaskTick) lastTaskTick = tick;
        }

        long requestSpan = (lastTaskTick - firstTaskTick);

        return strategy.computeNegotiationPriority(greenScore, cooperationScore, firstTaskTick, requestSpan);
    }

    public Double computePostponementPriority(AID aid, double energyToFree) {
        double greenScore = greenScores.getOrDefault(aid, 0.0);
        double cooperationScore = cooperationScores.getOrDefault(aid, 0.5);

        return strategy.computePostponementPriority(greenScore, cooperationScore, energyToFree);
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

    /**
     * Updates the green energy score using an Exponential Moving Average (EMA).
     * New Score = (0.8 * OldScore) + (0.2 * CurrentRatio)
     */
    public void updateGreenEnergyScore(AID householdId, double greenUsed, double externalUsed) {
        double total = greenUsed + externalUsed;
        double ratio = (total > 0) ? (greenUsed / total) : 0.0;

        double currentScore = greenScores.getOrDefault(householdId, 0.0); // Default to 0 initially

        // EMA smoothing factor (0.2 means the new reading has 20% weight)
        double alpha = 0.2;
        double newScore = (1.0 - alpha) * currentScore + (alpha * ratio);

        greenScores.put(householdId, newScore);
    }

    /**
     * General cooperation update (small steps).
     */
    public void updateCooperationScore(AID householdId, boolean accepted) {
        double current = cooperationScores.getOrDefault(householdId, 0.5);
        double updated = accepted
                ? Math.min(1.0, current + 0.05)
                : Math.max(0.0, current - 0.03);
        cooperationScores.put(householdId, updated);
    }

    /**
     * Significant reward for postponing a task during a panic.
     * Adds +0.2 to the score, capped at 1.0.
     */
    public void rewardPostponement(AID householdId) {
        double current = cooperationScores.getOrDefault(householdId, 0.5);
        double updated = Math.min(1.0, current + 0.2);
        cooperationScores.put(householdId, updated);
        log("Rewarded agent " + householdId.getLocalName() + " for postponement. New CoopScore: " + updated, LogSeverity.DEBUG, this);
    }

    public double getPredictedMaxAmount(long tick) {
        double predictedMax = predictionModel.predictAvailableEnergy(tick);
        log("Predicting " + predictedMax + " energy for tick " + tick, LogSeverity.DEBUG, this);
        return predictedMax;
    }

    public void logCurrentAverageProduction() {
        double avg = ((MovingAveragePredictionModel) predictionModel).calculateAverageProduction();
        log("Current average production " + avg, LogSeverity.DEBUG, this);
    }

    public void updatePredictionModel(double production, double batteryCharge) {
        predictionModel.update(production, batteryCharge);
    }

    public Map<Long, Double> calculateAverageProduction(long startTick, long endTick, LongFunction<Double> loadPerTickProvider) {
        return predictionModel.simulateEnergyBalances(startTick, endTick, loadPerTickProvider);
    }
}