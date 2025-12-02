package edu.wut.thesis.smart_energy_community_abm.agents;

import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.SimulationTickBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.AllocationEntry;
import edu.wut.thesis.smart_energy_community_abm.domain.EnergyRequest;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
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
    public Integer agentCount;
    public double runningAvgProduction = 0.0;
    public long productionSampleCount = 0;

    @Override
    protected void setup() {
        super.setup();

        final Object[] args = getArguments();

        agentCount = (Integer) args[0];

        if (agentCount == null) {
            log("Agent count is missing", LogSeverity.ERROR, this);
            throw new RuntimeException("Agent count is missing");
        }

        addBehaviour(new SimulationTickBehaviour(this));
    }

    public double computePriority(EnergyRequest req, long currentTick) {
        double greenScore = greenScores.getOrDefault(req.sourceId(), 0.5);
        double greenScoreWeight = 1.0 - greenScore; // low = needs help

        long reservationAge = currentTick - req.requestTimestamp();
        double reservationBonus = 1.0 - Math.exp(-reservationAge / 50.0);

        return (greenScoreWeight * 0.4) + (reservationBonus * 0.6);
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
}
