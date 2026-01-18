package edu.wut.thesis.smart_energy_community_abm.domain.dto;

import edu.wut.thesis.smart_energy_community_abm.domain.timeseries.AggregatedMetric;
import edu.wut.thesis.smart_energy_community_abm.domain.timeseries.Metric;

public record MetricResponse(long tick, double value) {
    public static MetricResponse from(Metric metric) {
        return new MetricResponse(metric.getTimestamp(), metric.getValue());
    }

    public static MetricResponse from(AggregatedMetric metric) {
        return new MetricResponse(metric.getTimestamp(), metric.getValue());
    }
}