package edu.wut.thesis.smart_energy_community_abm.agents;

import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.Phase1Behaviour;
import jade.core.AID;
import jade.core.ServiceException;
import jade.core.messaging.TopicManagementHelper;

public class ApplianceAgent extends BaseAgent {

    @Override
    protected void setup() {
        super.setup();

        try {
            TopicManagementHelper topicHelper = (TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);
            AID topic = topicHelper.createTopic("TICK");
            topicHelper.register(topic);

            addBehaviour(new Phase1Behaviour(this));

        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }
}
