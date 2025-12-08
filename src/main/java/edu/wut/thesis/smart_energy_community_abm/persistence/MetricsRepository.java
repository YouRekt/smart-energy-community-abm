package edu.wut.thesis.smart_energy_community_abm.persistence;

import edu.wut.thesis.smart_energy_community_abm.domain.timeseries.Metric;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetricsRepository extends JpaRepository<Metric, Metric.MetricID> {
}
