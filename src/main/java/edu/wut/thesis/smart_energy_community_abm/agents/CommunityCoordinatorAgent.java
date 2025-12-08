package edu.wut.thesis.smart_energy_community_abm.agents;

import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.SimulationTickBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.AllocationEntry;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import edu.wut.thesis.smart_energy_community_abm.domain.strategy.GraceContext;
import edu.wut.thesis.smart_energy_community_abm.domain.strategy.PanicContext;
import edu.wut.thesis.smart_energy_community_abm.domain.strategy.PriorityContext;
import edu.wut.thesis.smart_energy_community_abm.domain.strategy.interfaces.NegotiationStrategy;
import jade.core.AID;

import java.util.*;

public final class CommunityCoordinatorAgent extends BaseAgent {
    public final List<AID> householdAgents = new ArrayList<>();
    public final List<AID> energyAgents = new ArrayList<>();
    public final Map<AID, Double> greenScores = new HashMap<>();
    public final TreeMap<Long, Map<AID, AllocationEntry>> allocations = new TreeMap<>();
    public AID batteryAgent;
    public boolean energyPanic = false;
    public Double minChargeThreshold = 0.0;
    public long tick = 0;
    public short phase = 1;
    public Integer householdCount;
    public Integer energySourceCount;
    public double runningAvgProduction = 0.0;
    public long productionSampleCount = 0;
    public NegotiationStrategy strategy;

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

        if(energySourceCount == null) {
            log("Energy Agent count is missing", LogSeverity.ERROR, this);
            throw new RuntimeException("Energy Agent count is missing");
        }

        strategy = (NegotiationStrategy) args[2];

        log("Using Negotiation Strategy: " + strategy.getName(), LogSeverity.DEBUG, this);

        addBehaviour(new SimulationTickBehaviour(this));
    }

    public double computePriority(AllocationEntry entry, long currentTick) {
        double greenScore = greenScores.getOrDefault(entry.requesterId(), 0.5);
        double totalEnergy = entry.requestedEnergy();  // For single tick; multiply by duration if multi-tick

        PriorityContext ctx = new PriorityContext(entry, currentTick, greenScore, totalEnergy);
        return strategy.computePriority(ctx);
    }

    public boolean shouldGrantBatteryGrace(AID householdId, double requestedEnergy, double remainingShortfall, double batteryCharge) {
        double greenScore = greenScores.getOrDefault(householdId, 0.5);

        GraceContext ctx = new GraceContext(householdId, greenScore, requestedEnergy, remainingShortfall, batteryCharge);
        return strategy.shouldGrantBatteryGrace(ctx);
    }

    public boolean shouldTriggerPanic(double shortfall, double batteryCharge, int householdsAffected) {
        PanicContext ctx = new PanicContext(shortfall, batteryCharge, minChargeThreshold, householdsAffected);
        return strategy.shouldTriggerPanic(ctx);
    }

    public double getAllocatedAt(long tick) {
        return allocations.getOrDefault(tick, Map.of())
                .values().stream()
                .mapToDouble(AllocationEntry::grantedEnergy)
                .sum();
    }

    public void updateRunningAverage(double production) {
        runningAvgProduction += (production - runningAvgProduction) / ++productionSampleCount;
    }

    public void addAllocation(long tick, AID householdId, AllocationEntry entry) {
        allocations.computeIfAbsent(tick, _ -> new HashMap<>()).put(householdId, entry);
    }

    public void removeAllocation(long tick, AID householdId) {
        Map<AID, AllocationEntry> tickAllocs = allocations.get(tick);
        if (tickAllocs != null) {
            tickAllocs.remove(householdId);
            if (tickAllocs.isEmpty()) {
                allocations.remove(tick);
            }
        }
    }
}
