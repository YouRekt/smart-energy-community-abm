package edu.wut.thesis.smart_energy_community_abm.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.metamodel.EntityType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimescaleTableInitializer implements ApplicationListener<ApplicationReadyEvent> {

    @PersistenceContext
    private final EntityManager entityManager;
    private final TransactionTemplate transactionTemplate;

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();

        for (EntityType<?> entity : entities) {
            Class<?> javaType = entity.getJavaType();

            if (javaType.isAnnotationPresent(TimescaleTable.class)) {
                TimescaleTable annotation = javaType.getAnnotation(TimescaleTable.class);
                String tableName = annotation.tableName();
                String timeColumn = annotation.timeColumnName();

                log.info("Found TimescaleDB table candidate: {}", tableName);

                initHypertable(tableName, timeColumn);

                if ("metrics".equals(tableName)) {
                    configureMetricsOptimization(tableName, timeColumn);
                }
            }
        }
    }

    private void initHypertable(String tableName, String timeColumn) {
        try {
            transactionTemplate.execute(_ -> {
                String sql = String.format("SELECT create_hypertable('%s', '%s', if_not_exists => TRUE);", tableName, timeColumn);
                entityManager.createNativeQuery(sql).getResultList();
                return null;
            });
            log.info("Verified Hypertable for: {}", tableName);
        } catch (Exception e) {
            log.error("Failed to init Hypertable for {}", tableName, e);
        }
    }

    private void configureMetricsOptimization(String tableName, String timeColumn) {
        transactionTemplate.execute(_ -> {
            try {
                entityManager.createNativeQuery(String.format("""
                            ALTER TABLE %s SET (
                                timescaledb.compress,
                                timescaledb.compress_segmentby = 'name',
                                timescaledb.compress_orderby = '%s'
                            );
                        """, tableName, timeColumn)).executeUpdate();
                log.info("Compression enabled for: {}", tableName);
            } catch (Exception e) {
                log.debug("Compression likely already active for {}", tableName);
            }

            try {
                entityManager.createNativeQuery(String.format("""
                            CREATE INDEX IF NOT EXISTS idx_%s_name_time_desc
                            ON %s (name, %s DESC);
                        """, tableName, tableName, timeColumn)).executeUpdate();
                log.info("Dashboard performance index created for: {}", tableName);
            } catch (Exception e) {
                log.error("Failed to create index for {}", tableName, e);
            }
            return null;
        });
    }
}