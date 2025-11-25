package edu.wut.thesis.smart_energy_community_abm.domain;

import jade.core.AID;
import jade.core.Agent;
import jade.core.ServiceException;
import jade.core.messaging.TopicManagementHelper;

public final class TopicHelper {
    public static AID getTopic(Agent agent, String topicName) throws ServiceException {
        TopicManagementHelper topicHelper = (TopicManagementHelper) agent.getHelper(TopicManagementHelper.SERVICE_NAME);

        return topicHelper.createTopic(topicName);
    }
}
