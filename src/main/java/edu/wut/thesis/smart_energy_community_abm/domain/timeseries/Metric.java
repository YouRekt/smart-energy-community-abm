package edu.wut.thesis.smart_energy_community_abm.domain.timeseries;

import edu.wut.thesis.smart_energy_community_abm.config.TimescaleTable;
import lombok.*;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = Metric.TABLE_NAME)
@TimescaleTable(tableName = Metric.TABLE_NAME, timeColumnName = Metric.TIME_COLUMN_NAME)
@IdClass(Metric.MetricID.class)
public final class Metric {
    public static final String TABLE_NAME = "metrics";
    public static final String TIME_COLUMN_NAME = "time";

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetricID implements Serializable {
        private Integer id;
        private LocalDateTime time;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "metrics_seq_gen")
    @SequenceGenerator(name = "metrics_seq_gen", sequenceName = "metrics_id_seq", allocationSize = 1000)
    private Integer id;

    @Id
    @Column(name = TIME_COLUMN_NAME, nullable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double value;

    @Column(nullable = false)
    private long timestamp;
}