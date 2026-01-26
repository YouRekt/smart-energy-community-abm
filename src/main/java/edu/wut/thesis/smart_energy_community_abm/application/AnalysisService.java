package edu.wut.thesis.smart_energy_community_abm.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wut.thesis.smart_energy_community_abm.domain.config.CommunityConfig;
import edu.wut.thesis.smart_energy_community_abm.domain.config.HouseholdConfig;
import edu.wut.thesis.smart_energy_community_abm.domain.dto.SimulationAnalysisResponse;
import edu.wut.thesis.smart_energy_community_abm.domain.simulation.SimulationRun;
import edu.wut.thesis.smart_energy_community_abm.persistence.MetricsRepository;
import edu.wut.thesis.smart_energy_community_abm.persistence.SimulationRunRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static edu.wut.thesis.smart_energy_community_abm.domain.util.MetricNameHelper.*;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final MetricsRepository metricsRepository;
    private final SimulationRunRepository runRepository;

    public SimulationAnalysisResponse analyzeRun(Long runId) {
        SimulationRun run = runRepository.findById(runId)
                .orElseThrow(() -> new NoSuchElementException("Run not found: " + runId));

        if (run.getEndTime() == null) {
            throw new IllegalStateException("Cannot analyze a running simulation");
        }

        final ObjectMapper mapper = new ObjectMapper();

        try {
            final CommunityConfig config = mapper.readValue(run.getConfigJson(), new TypeReference<>() {
            });

            LocalDateTime start = run.getStartTime();
            LocalDateTime end = run.getEndTime();

            // Energy metrics
            double greenConsumed = sum(COMMUNITY_GREEN_CONSUMPTION, start, end);
            double totalConsumed = sum(COMMUNITY_TOTAL_CONSUMPTION, start, end);
            double totalProduced = sum(COMMUNITY_PRODUCTION, start, end);

            double selfSufficiencyRatio = safePercent(greenConsumed, totalConsumed);
            double selfConsumptionRatio = safePercent(greenConsumed, totalProduced);

            // Grid metrics
            Double maxGridPeak = metricsRepository.maxByPattern(COMMUNITY_GRID_CONSUMPTION, start, end);
            Double gridVolatility = metricsRepository.stdDevByPattern(COMMUNITY_GRID_CONSUMPTION, start, end);

            // Battery metrics
            double totalDischarged = sum(BATTERY_DISCHARGE_AMOUNT, start, end);
            double totalCharged = sum(BATTERY_CHARGE_AMOUNT, start, end);
            double totalCurtailed = sum(BATTERY_CURTAILED, start, end);
            double batteryCapacity = config.batteryConfig().capacity();

            double equivalentFullCycles = safeDiv(totalDischarged, batteryCapacity);
            double batteryEfficiency = safePercent(totalDischarged, totalCharged);
            double energyLossRatio = safePercent(totalCurtailed, totalProduced);

            // Battery time metrics
            double timeFull = sum(BATTERY_FULL, start, end);
            double timeEmpty = sum(BATTERY_EMPTY, start, end);
            Long ticks = metricsRepository.getSimulationLengthInTicks(start, end);
            double totalTime = ticks != null ? ticks.doubleValue() : 0;

            double fullRatio = safePercent(timeFull, totalTime);
            double emptyRatio = safePercent(timeEmpty, totalTime);

            // Task metrics
            double totalRequested = sum(TOTAL_REQUESTED, start, end);
            double totalAccepted = sum(TOTAL_ACCEPTED, start, end);
            double totalFinished = sum(TOTAL_FINISHED, start, end);

            double taskCompletionRate = safePercent(totalFinished, totalAccepted);
            double taskAcceptanceRate = safePercent(totalAccepted, totalRequested);

            // Fairness (Jain's Index)
            List<String> householdNames = config.householdConfigs().stream().map(HouseholdConfig::householdName).toList();
            List<Double> greenRatios = householdNames.stream()
                    .map(h -> {
                        double green = sum(householdGreenConsumption(h), start, end);
                        double grid = sum(householdGridConsumption(h), start, end);
                        return safeDiv(green, green + grid);
                    })
                    .toList();

            double fairnessIndex = computeJainFairness(greenRatios);

            return new SimulationAnalysisResponse(
                    config.tickConfig().getTickMultiplier(),
                    selfSufficiencyRatio,
                    selfConsumptionRatio,
                    maxGridPeak != null ? maxGridPeak : 0.0,
                    gridVolatility != null ? gridVolatility : 0.0,
                    equivalentFullCycles,
                    batteryEfficiency,
                    energyLossRatio,
                    fullRatio,
                    emptyRatio,
                    taskCompletionRate,
                    taskAcceptanceRate,
                    fairnessIndex
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private double sum(String pattern, LocalDateTime start, LocalDateTime end) {
        Double val = metricsRepository.sumByPattern(pattern, start, end);
        return val != null ? val : 0.0;
    }

    private double safeDiv(double a, double b) {
        return b == 0 ? 0.0 : a / b;
    }

    private double safePercent(double a, double b) {
        return b == 0 ? 0.0 : (a / b) * 100;
    }

    private double computeJainFairness(List<Double> values) {
        if (values.isEmpty()) return 1.0;
        int n = values.size();
        double sum = values.stream().mapToDouble(Double::doubleValue).sum();
        double sumSquares = values.stream().mapToDouble(v -> v * v).sum();
        return sumSquares == 0 ? 1.0 : (sum * sum) / (n * sumSquares);
    }
}