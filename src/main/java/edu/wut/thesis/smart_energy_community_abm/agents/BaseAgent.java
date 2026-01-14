package edu.wut.thesis.smart_energy_community_abm.agents;

import edu.wut.thesis.smart_energy_community_abm.config.SpringContext;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public abstract class BaseAgent extends Agent {
    private final static Logger logger = LoggerFactory.getLogger(BaseAgent.class);

    @Override
    protected void setup() {
        super.setup();

        ApplicationContext context = SpringContext.getApplicationContext();
        if (context != null) {
            // get beans
        } else {
            throw new RuntimeException("Application Context is null");
        }
    }

    // TODO: Improve logging by appending currently executed behaviour at time of log
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
