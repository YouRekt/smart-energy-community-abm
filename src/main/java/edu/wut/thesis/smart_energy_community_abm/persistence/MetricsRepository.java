package edu.wut.thesis.smart_energy_community_abm.persistence;

import edu.wut.thesis.smart_energy_community_abm.domain.timeseries.AggregatedMetric;
import edu.wut.thesis.smart_energy_community_abm.domain.timeseries.Metric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MetricsRepository extends JpaRepository<Metric, Metric.MetricID> {
    List<Metric> findAllByNameOrderByTimestampAsc(String name);
    List<Metric> findAllByNameAndTimestampGreaterThanOrderByTimestampAsc(String name, long lastTick);

    @Query(value = """
        SELECT m.timestamp as timestamp, SUM(m.value) as value
        FROM metrics m
        WHERE m.name LIKE :namePattern
        GROUP BY m.timestamp
        ORDER BY m.timestamp ASC
        """, nativeQuery = true)
    List<AggregatedMetric> findAggregateSumByNamePattern(@Param("namePattern") String namePattern);

    @Query(value = """
        SELECT m.timestamp as timestamp, SUM(m.value) as value
        FROM metrics m
        WHERE m.name LIKE :namePattern
        AND m.timestamp > :minTick
        GROUP BY m.timestamp
        ORDER BY m.timestamp ASC
        """, nativeQuery = true)
    List<AggregatedMetric> findAggregateSumByNamePatternSince(@Param("namePattern") String namePattern, @Param("minTick") long minTick);
}
