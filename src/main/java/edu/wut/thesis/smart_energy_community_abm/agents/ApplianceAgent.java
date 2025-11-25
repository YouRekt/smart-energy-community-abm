package edu.wut.thesis.smart_energy_community_abm.agents;

import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.MessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.LogSeverity;
import jade.core.AID;
import jade.core.ServiceException;
import jade.core.messaging.TopicManagementHelper;

public final class ApplianceAgent extends BaseAgent {
    public String coordinatorName;

    @Override
    protected void setup() {
        super.setup();

        final Object[] args = getArguments();

        coordinatorName = (String) args[0];

        if (coordinatorName == null) {
            log("Household Coordinator's name is missing", LogSeverity.ERROR);
            doDelete();
            throw new RuntimeException("Household Coordinator's name is missing");
        }

        try {
            TopicManagementHelper topicHelper = (TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);
            AID topic = topicHelper.createTopic(coordinatorName);
            topicHelper.register(topic);
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }

        addBehaviour(new MessageHandlerBehaviour(this));
    }
}
