package edu.wut.thesis.smart_energy_community_abm.domain.dto;

import java.util.List;

public record StackedMetricResponse(
        List<MetricResponse> green,
        List<MetricResponse> grid
) {}