package edu.wut.thesis.smart_energy_community_abm.domain.timeseries;

public interface AggregatedMetric {
    long getTimestamp();
    double getValue();
}