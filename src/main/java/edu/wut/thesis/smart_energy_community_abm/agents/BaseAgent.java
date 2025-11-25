package edu.wut.thesis.smart_energy_community_abm.agents;

import edu.wut.thesis.smart_energy_community_abm.config.SpringContext;
import edu.wut.thesis.smart_energy_community_abm.domain.LogSeverity;
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

    public void log(String message, LogSeverity severity) {
        switch (severity) {
            case WARNING:
                logger.warn("{}", message);
                break;
            case ERROR:
                logger.error("{}", message);
                break;
            case TRACE:
                logger.trace("{}", message);
                break;
            case DEBUG:
                logger.debug("{}", message);
                break;
            default:
                logger.info("{}", message);
        }
    }
}
