package edu.wut.thesis.smart_energy_community_abm.agents;

import edu.wut.thesis.smart_energy_community_abm.config.SpringContext;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import edu.wut.thesis.smart_energy_community_abm.domain.timeseries.Metric;
import edu.wut.thesis.smart_energy_community_abm.persistence.MetricsRepository;
import jade.core.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public abstract class BaseAgent extends Agent {
    private final static Logger logger = LoggerFactory.getLogger(BaseAgent.class);
    public long tick = 0;
    private MetricsRepository metricsRepository;

    @Override
    protected void setup() {
        super.setup();

        metricsRepository = SpringContext.getBean(MetricsRepository.class);
    }

    protected void pushMetric(String name, double value) {
        metricsRepository.save(Metric.builder()
                .name(name)
                .value(value)
                .timestamp(tick)
                .time(LocalDateTime.now())
                .build());
    }

    public void log(String message, LogSeverity severity, Object reference) {
        String format = "@ [{}] - {}";

        String fixedWidthName = String.format("%-25.25s", reference.getClass().getSimpleName().replace("Behaviour", ""));

        switch (severity) {
            case WARN:
                logger.warn(format, fixedWidthName, message);
                break;
            case ERROR:
                logger.error(format, fixedWidthName, message);
                break;
            case TRACE:
                logger.trace(format, fixedWidthName, message);
                break;
            case DEBUG:
                logger.debug(format, fixedWidthName, message);
                break;
            default:
                logger.info(format, fixedWidthName, message);
        }
    }
}
