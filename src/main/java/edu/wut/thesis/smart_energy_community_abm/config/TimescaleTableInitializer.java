package edu.wut.thesis.smart_energy_community_abm.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Set;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimescaleTableInitializer implements ApplicationListener<ApplicationReadyEvent> {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(TimescaleTable.class));

        Set<BeanDefinition> beans = scanner.findCandidateComponents("edu.wut.thesis.smart_energy_community_abm.domain");

        for (BeanDefinition bean : beans) {
            try {
                Class<?> clazz = Class.forName(bean.getBeanClassName());
                TimescaleTable annotation = clazz.getAnnotation(TimescaleTable.class);
                String tableName = annotation.tableName();
                String timeColumn = annotation.timeColumnName();

                log.info("Initializing TimescaleDB Hypertable for table: {}", tableName);

                initHypertable(tableName, timeColumn);

                if ("metrics".equals(tableName)) {
                    configureMetricsOptimization(tableName, timeColumn);
                }

            } catch (ClassNotFoundException e) {
                log.error("Failed to initialize Timescale table for bean: {}", bean.getBeanClassName(), e);
            }
        }
    }

    private void initHypertable(String tableName, String timeColumn) {
        String sql = String.format("SELECT create_hypertable('%s', '%s', if_not_exists => TRUE);", tableName, timeColumn);
        entityManager.createNativeQuery(sql).getResultList();
    }

    private void configureMetricsOptimization(String tableName, String timeColumn) {
        try {
            entityManager.createNativeQuery(String.format("""
                ALTER TABLE %s SET (
                    timescaledb.compress,
                    timescaledb.compress_segmentby = 'name',
                    timescaledb.compress_orderby = '%s DESC'
                );
            """, tableName, timeColumn)).executeUpdate();
            log.info("Compression enabled for table: {}", tableName);
        } catch (Exception e) {
            log.debug("Compression likely already enabled for {}", tableName);
        }

        try {
            entityManager.createNativeQuery(String.format("""
                SELECT add_compression_policy('%s', INTERVAL '1 day', if_not_exists => TRUE);
            """, tableName)).getResultList();
            log.info("Compression policy added for table: {}", tableName);
        } catch (Exception e) {
            log.error("Could not add compression policy", e);
        }

        try {
            entityManager.createNativeQuery(String.format("""
                CREATE INDEX IF NOT EXISTS idx_%s_name_time
                ON %s (name, %s DESC);
            """, tableName, tableName, timeColumn)).executeUpdate();
            log.info("Custom index created for table: {}", tableName);
        } catch (Exception e) {
            log.error("Could not create index", e);
        }
    }
}