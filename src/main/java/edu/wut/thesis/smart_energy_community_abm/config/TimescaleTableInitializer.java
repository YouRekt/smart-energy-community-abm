package edu.wut.thesis.smart_energy_community_abm.config;

import java.util.Set;

import lombok.RequiredArgsConstructor;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public final class TimescaleTableInitializer {
    private final EntityManager entityManager;

    private void createHypertable(String tableName, String timeColumnName) {
        entityManager
                .createNativeQuery(String.format(
                        "SELECT create_hypertable('%s','%s', if_not_exists => TRUE);",
                        tableName,
                        timeColumnName
                ))
                .getResultList();
    }

    @PostConstruct
    public void init() {
        // get all entities
        Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();

        // for each entity
        for (EntityType<?> entity : entities) {
            // get entity class
            Class<?> javaType = entity.getJavaType();

            // check of TimescaleTable annotation
            if (javaType.isAnnotationPresent(TimescaleTable.class)) {
                // get metadata from annotation
                TimescaleTable annotation = javaType.getAnnotation(TimescaleTable.class);
                String tableName = annotation.tableName();
                String timeColumnName = annotation.timeColumnName();

                // create hypertable
                createHypertable(tableName, timeColumnName);
            }
        }
    }
}
