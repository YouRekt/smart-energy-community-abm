package edu.wut.thesis.smart_energy_community_abm.agents;

import edu.wut.thesis.smart_energy_community_abm.config.SpringContext;
import jade.core.Agent;
import org.springframework.context.ApplicationContext;

public abstract class BaseAgent extends Agent {

    @Override
    protected void setup() {
        super.setup();

        ApplicationContext context = SpringContext.getApplicationContext();
        if(context != null) {
            // get beans
        } else {
            throw new RuntimeException("Application Context is null");
        }
    }
}
