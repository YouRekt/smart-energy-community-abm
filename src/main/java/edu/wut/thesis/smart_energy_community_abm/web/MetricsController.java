package edu.wut.thesis.smart_energy_community_abm.web;

import edu.wut.thesis.smart_energy_community_abm.domain.dto.MetricResponse;
import edu.wut.thesis.smart_energy_community_abm.domain.dto.StackedMetricResponse;
import edu.wut.thesis.smart_energy_community_abm.domain.util.MetricNameHelper;
import edu.wut.thesis.smart_energy_community_abm.persistence.MetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public final class MetricsController {
    private final MetricsRepository metricsRepository;

    @GetMapping("/consumption/community")
    public ResponseEntity<StackedMetricResponse> getCommunityConsumption(
            @RequestParam(required = false) Long lastTick
    ) {
        return ResponseEntity.ok(getStackedMetrics(
                MetricNameHelper.communityGreenConsumption(),
                MetricNameHelper.communityGridConsumption(),
                lastTick,
                true
        ));
    }

    @GetMapping("/consumption/household/{householdName}")
    public ResponseEntity<StackedMetricResponse> getHouseholdConsumption(
            @PathVariable String householdName,
            @RequestParam(required = false) Long lastTick
    ) {
        return ResponseEntity.ok(getStackedMetrics(
                MetricNameHelper.householdGreenConsumption(householdName),
                MetricNameHelper.householdGridConsumption(householdName),
                lastTick,
                true
        ));
    }

    @GetMapping("/consumption/appliance/{householdName}/{applianceName}")
    public ResponseEntity<StackedMetricResponse> getApplianceConsumption(
            @PathVariable String householdName,
            @PathVariable String applianceName,
            @RequestParam(required = false) Long lastTick
    ) {
        return ResponseEntity.ok(getStackedMetrics(
                MetricNameHelper.applianceGreenConsumption(householdName, applianceName),
                MetricNameHelper.applianceGridConsumption(householdName, applianceName),
                lastTick,
                false
        ));
    }

    @GetMapping("/production/community")
    public ResponseEntity<List<MetricResponse>> getCommunityProduction(
            @RequestParam(required = false) Long lastTick
    ) {
        return ResponseEntity.ok(getMetrics(
                MetricNameHelper.communityTotalProduction(),
                lastTick,
                true
        ));
    }

    @GetMapping("/production/source/{sourceName}")
    public ResponseEntity<List<MetricResponse>> getSourceProduction(
            @PathVariable String sourceName,
            @RequestParam(required = false) Long lastTick
    ) {
        return ResponseEntity.ok(getMetrics(
                MetricNameHelper.sourceProduction(sourceName),
                lastTick,
                false
        ));
    }

    @GetMapping("/battery/charge")
    public ResponseEntity<List<MetricResponse>> getBatteryCharge(
            @RequestParam(required = false) Long lastTick
    ) {
        return ResponseEntity.ok(getMetrics(
                MetricNameHelper.BATTERY_CHARGE,
                lastTick,
                false
        ));
    }

    private StackedMetricResponse getStackedMetrics(String greenPattern, String gridPattern, Long lastTick, boolean isAggregate) {
        List<MetricResponse> green = getMetrics(greenPattern, lastTick, isAggregate);
        List<MetricResponse> grid = getMetrics(gridPattern, lastTick, isAggregate);

        return new StackedMetricResponse(green, grid);
    }

    private List<MetricResponse> getMetrics(String pattern, Long lastTick, boolean isAggregate) {
        if (lastTick == null || lastTick < 0) {
            if (isAggregate) {
                return metricsRepository.findAggregateSumByNamePattern(pattern)
                        .stream().map(MetricResponse::from).toList();
            } else {
                return metricsRepository.findAllByNameOrderByTimestampAsc(pattern)
                        .stream().map(MetricResponse::from).toList();
            }
        } else {
            if (isAggregate) {
                return metricsRepository.findAggregateSumByNamePatternSince(pattern, lastTick)
                        .stream().map(MetricResponse::from).toList();
            } else {
                return metricsRepository.findAllByNameAndTimestampGreaterThanOrderByTimestampAsc(pattern, lastTick)
                        .stream().map(MetricResponse::from).toList();
            }
        }
    }
}