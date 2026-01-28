package edu.wut.thesis.smart_energy_community_abm.agents;

import edu.wut.thesis.smart_energy_community_abm.config.SpringContext;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import edu.wut.thesis.smart_energy_community_abm.domain.simulation.SimulationState;
import edu.wut.thesis.smart_energy_community_abm.domain.timeseries.Metric;
import edu.wut.thesis.smart_energy_community_abm.persistence.MetricsRepository;
import jade.core.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BaseAgent extends Agent {
    private final static Logger logger = LoggerFactory.getLogger(BaseAgent.class);

    private final ExecutorService dbExecutor = Executors.newSingleThreadExecutor();

    public long tick = 0;

    protected Random rand;

    private MetricsRepository metricsRepository;

    @Override
    protected void setup() {
        super.setup();

        SimulationState simulationState = SpringContext.getBean(SimulationState.class);
        metricsRepository = SpringContext.getBean(MetricsRepository.class);

        rand = new Random(simulationState.getCurrentRunRandomSeed());
    }

    @Override
    protected void takeDown() {
        if (dbExecutor != null && !dbExecutor.isShutdown()) {
            dbExecutor.shutdown();
        }
        super.takeDown();
    }

    protected void pushMetric(String name, double value) {
        final String n = name;
        final double val = value;
        final long t = tick;
        final LocalDateTime now = LocalDateTime.now();
        dbExecutor.submit(() -> {
            try {
                metricsRepository.save(Metric.builder()
                        .name(n)
                        .value(val)
                        .timestamp(t)
                        .time(now)
                        .build());
            } catch (Exception e) {
                log("Failed to push metric: " + e.getMessage(), LogSeverity.ERROR, this);
            }
        });
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
