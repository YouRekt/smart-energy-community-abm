package edu.wut.thesis.smart_energy_community_abm.web;

import edu.wut.thesis.smart_energy_community_abm.domain.dto.MetricResponse;
import edu.wut.thesis.smart_energy_community_abm.domain.dto.StackedMetricResponse;
import edu.wut.thesis.smart_energy_community_abm.domain.simulation.SimulationRun;
import edu.wut.thesis.smart_energy_community_abm.domain.simulation.SimulationState;
import edu.wut.thesis.smart_energy_community_abm.domain.util.MetricNameHelper;
import edu.wut.thesis.smart_energy_community_abm.persistence.MetricsRepository;
import edu.wut.thesis.smart_energy_community_abm.persistence.SimulationRunRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static edu.wut.thesis.smart_energy_community_abm.domain.util.MetricNameHelper.*;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public final class MetricsController {
    private final MetricsRepository metricsRepository;
    private final SimulationRunRepository runRepository;
    private final SimulationState simulationState;

    @GetMapping("/runs")
    public ResponseEntity<List<SimulationRun>> getRunHistory() {
        return ResponseEntity.ok(runRepository.findAllByOrderByStartTimeDesc());
    }

    @GetMapping("/consumption/community")
    public ResponseEntity<StackedMetricResponse> getCommunityConsumption(
            @RequestParam(required = false) Long lastTick,
            @RequestParam(required = false) Long runId,
            @RequestParam(required = false, defaultValue = "0") Integer limit
    ) {
        return ResponseEntity.ok(getStackedMetrics(
                COMMUNITY_GREEN_CONSUMPTION,
                COMMUNITY_GRID_CONSUMPTION,
                lastTick, runId, limit, true
        ));
    }

    @GetMapping("/consumption/household/{householdName}")
    public ResponseEntity<StackedMetricResponse> getHouseholdConsumption(
            @PathVariable String householdName,
            @RequestParam(required = false) Long lastTick,
            @RequestParam(required = false) Long runId,
            @RequestParam(required = false, defaultValue = "0") Integer limit
    ) {
        return ResponseEntity.ok(getStackedMetrics(
                MetricNameHelper.householdGreenConsumption(householdName),
                MetricNameHelper.householdGridConsumption(householdName),
                lastTick, runId, limit, true
        ));
    }

    @GetMapping("/consumption/appliance/{householdName}/{applianceName}")
    public ResponseEntity<StackedMetricResponse> getApplianceConsumption(
            @PathVariable String householdName,
            @PathVariable String applianceName,
            @RequestParam(required = false) Long lastTick,
            @RequestParam(required = false) Long runId,
            @RequestParam(required = false, defaultValue = "0") Integer limit
    ) {
        return ResponseEntity.ok(getStackedMetrics(
                MetricNameHelper.applianceGreenConsumption(householdName, applianceName),
                MetricNameHelper.applianceGridConsumption(householdName, applianceName),
                lastTick, runId, limit, false
        ));
    }

    @GetMapping("/production/community")
    public ResponseEntity<List<MetricResponse>> getCommunityProduction(
            @RequestParam(required = false) Long lastTick,
            @RequestParam(required = false) Long runId,
            @RequestParam(required = false, defaultValue = "0") Integer limit
    ) {
        return ResponseEntity.ok(getMetrics(
                COMMUNITY_PRODUCTION,
                lastTick, runId, limit, true
        ));
    }

    @GetMapping("/production/source/{sourceName}")
    public ResponseEntity<List<MetricResponse>> getSourceProduction(
            @PathVariable String sourceName,
            @RequestParam(required = false) Long lastTick,
            @RequestParam(required = false) Long runId,
            @RequestParam(required = false, defaultValue = "0") Integer limit
    ) {
        return ResponseEntity.ok(getMetrics(
                MetricNameHelper.sourceProduction(sourceName),
                lastTick, runId, limit, false
        ));
    }

    @GetMapping("/battery/charge")
    public ResponseEntity<List<MetricResponse>> getBatteryCharge(
            @RequestParam(required = false) Long lastTick,
            @RequestParam(required = false) Long runId,
            @RequestParam(required = false, defaultValue = "0") Integer limit
    ) {
        return ResponseEntity.ok(getMetrics(
                MetricNameHelper.BATTERY_CHARGE,
                lastTick, runId, limit, false
        ));
    }

    private StackedMetricResponse getStackedMetrics(String greenPattern, String gridPattern, Long lastTick, Long runId, Integer limit, boolean isAggregate) {
        List<MetricResponse> green = getMetrics(greenPattern, lastTick, runId, limit, isAggregate);
        List<MetricResponse> grid = getMetrics(gridPattern, lastTick, runId, limit, isAggregate);
        return new StackedMetricResponse(green, grid);
    }

    private List<MetricResponse> getMetrics(String pattern, Long lastTick, Long runId, Integer limit, boolean isAggregate) {
        LocalDateTime start;
        LocalDateTime end;
        boolean useWindowLimit = (limit != null && limit > 0);
        boolean useDelta = (lastTick != null && lastTick >= 0);

        if (runId != null) {
            SimulationRun run = runRepository.findById(runId)
                    .orElseThrow(() -> new NoSuchElementException("Run not found"));
            start = run.getStartTime();
            end = run.getEndTime(); // Null if currently running
        } else {
            start = simulationState.getCurrentRunStartTime();
            end = null;
        }

        List<MetricResponse> result;

        if (useWindowLimit) {
            if (isAggregate) {
                if (useDelta) {
                    result = metricsRepository.findAggregateSumByNamePatternSinceDesc(pattern, lastTick, limit)
                            .stream().map(MetricResponse::from).collect(Collectors.toList());
                } else if (end == null) {
                    result = metricsRepository.findAggregateSumByNamePatternAfterTimeDesc(pattern, start, limit)
                            .stream().map(MetricResponse::from).collect(Collectors.toList());
                } else {
                    result = metricsRepository.findAggregateSumByNamePatternBetweenDesc(pattern, start, end, limit)
                            .stream().map(MetricResponse::from).collect(Collectors.toList());
                }
            } else {
                Pageable pageable = PageRequest.of(0, limit, Sort.by("timestamp").descending());

                if (useDelta) {
                    result = metricsRepository.findAllByNameAndTimestampGreaterThan(pattern, lastTick, pageable)
                            .stream().map(MetricResponse::from).collect(Collectors.toList());
                } else if (end == null) {
                    result = metricsRepository.findAllByNameAndTimeGreaterThanEqual(pattern, start, pageable)
                            .stream().map(MetricResponse::from).collect(Collectors.toList());
                } else {
                    result = metricsRepository.findAllByNameAndTimeBetween(pattern, start, end, pageable)
                            .stream().map(MetricResponse::from).collect(Collectors.toList());
                }
            }

            Collections.reverse(result);
            return result;

        } else {
            if (useDelta) {
                if (isAggregate) {
                    return metricsRepository.findAggregateSumByNamePatternSince(pattern, lastTick)
                            .stream().map(MetricResponse::from).toList();
                } else {
                    return metricsRepository.findAllByNameAndTimestampGreaterThanOrderByTimestampAsc(pattern, lastTick)
                            .stream().map(MetricResponse::from).toList();
                }
            }

            if (isAggregate) {
                if (end == null) {
                    return metricsRepository.findAggregateSumByNamePatternAfterTime(pattern, start)
                            .stream().map(MetricResponse::from).toList();
                }
                return metricsRepository.findAggregateSumByNamePatternBetween(pattern, start, end)
                        .stream().map(MetricResponse::from).toList();
            } else {
                if (end == null) {
                    return metricsRepository.findAllByNameAndTimeGreaterThanEqualOrderByTimestampAsc(pattern, start)
                            .stream().map(MetricResponse::from).toList();
                }
                return metricsRepository.findAllByNameAndTimeBetweenOrderByTimestampAsc(pattern, start, end)
                        .stream().map(MetricResponse::from).toList();
            }
        }
    }
}