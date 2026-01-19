package edu.wut.thesis.smart_energy_community_abm.persistence;

import edu.wut.thesis.smart_energy_community_abm.domain.timeseries.AggregatedMetric;
import edu.wut.thesis.smart_energy_community_abm.domain.timeseries.Metric;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MetricsRepository extends JpaRepository<Metric, Metric.MetricID> {

    List<Metric> findAllByNameAndTimeGreaterThanEqual(String name, LocalDateTime start, Pageable pageable);

    List<Metric> findAllByNameAndTimeBetween(String name, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query(value = """
            SELECT m.timestamp as timestamp, SUM(m.value) as value
            FROM metrics m
            WHERE m.name LIKE :namePattern
            AND m.time >= :start
            GROUP BY m.timestamp
            ORDER BY m.timestamp DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<AggregatedMetric> findAggregateSumByNamePatternAfterTimeDesc(
            @Param("namePattern") String namePattern,
            @Param("start") LocalDateTime start,
            @Param("limit") int limit
    );

    @Query(value = """
            SELECT m.timestamp as timestamp, SUM(m.value) as value
            FROM metrics m
            WHERE m.name LIKE :namePattern
            AND m.time >= :start AND m.time <= :end
            GROUP BY m.timestamp
            ORDER BY m.timestamp DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<AggregatedMetric> findAggregateSumByNamePatternBetweenDesc(
            @Param("namePattern") String namePattern,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("limit") int limit
    );

    List<Metric> findAllByNameAndTimeGreaterThanEqualOrderByTimestampAsc(String name, LocalDateTime start);

    @Query(value = """
            SELECT m.timestamp as timestamp, SUM(m.value) as value
            FROM metrics m
            WHERE m.name LIKE :namePattern
            AND m.time >= :runStartTime
            GROUP BY m.timestamp
            ORDER BY m.timestamp ASC
            """, nativeQuery = true)
    List<AggregatedMetric> findAggregateSumByNamePatternAfterTime(
            @Param("namePattern") String namePattern,
            @Param("runStartTime") LocalDateTime runStartTime
    );

    List<Metric> findAllByNameAndTimeBetweenOrderByTimestampAsc(String name, LocalDateTime start, LocalDateTime end);

    @Query(value = """
            SELECT m.timestamp as timestamp, SUM(m.value) as value
            FROM metrics m
            WHERE m.name LIKE :namePattern
            AND m.time >= :start AND m.time <= :end
            GROUP BY m.timestamp
            ORDER BY m.timestamp ASC
            """, nativeQuery = true)
    List<AggregatedMetric> findAggregateSumByNamePatternBetween(
            @Param("namePattern") String namePattern,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    List<Metric> findAllByNameAndTimestampGreaterThanOrderByTimestampAsc(String name, long timestamp);

    @Query(value = """
            SELECT m.timestamp as timestamp, SUM(m.value) as value
            FROM metrics m
            WHERE m.name LIKE :namePattern
            AND m.timestamp > :minTick
            GROUP BY m.timestamp
            ORDER BY m.timestamp ASC
            """, nativeQuery = true)
    List<AggregatedMetric> findAggregateSumByNamePatternSince(
            @Param("namePattern") String namePattern,
            @Param("minTick") long minTick
    );

    List<Metric> findAllByNameAndTimestampGreaterThan(String name, long timestamp, Pageable pageable);

    @Query(value = """
            SELECT m.timestamp as timestamp, SUM(m.value) as value
            FROM metrics m
            WHERE m.name LIKE :namePattern
            AND m.timestamp > :minTick
            GROUP BY m.timestamp
            ORDER BY m.timestamp DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<AggregatedMetric> findAggregateSumByNamePatternSinceDesc(
            @Param("namePattern") String namePattern,
            @Param("minTick") long minTick,
            @Param("limit") int limit
    );
}