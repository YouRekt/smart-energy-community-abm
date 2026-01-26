package edu.wut.thesis.smart_energy_community_abm.domain.dto;

public record SimulationAnalysisResponse(
        long tickMultiplier,

        double selfSufficiencyRatio,
        double selfConsumptionRatio,

        double maxGridPeak,
        double gridVolatilityCV,

        double equivalentFullCycles,
        double batteryEfficiency,
        double energyLossRatio,
        double fullRatio,
        double emptyRatio,

        double taskCompletionRate,
        double taskAcceptanceRate,
        double fairnessIndex
) {}