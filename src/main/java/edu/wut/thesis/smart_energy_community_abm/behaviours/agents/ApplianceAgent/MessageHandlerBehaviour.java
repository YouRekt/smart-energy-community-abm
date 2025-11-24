package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.LogSeverity;
import edu.wut.thesis.smart_energy_community_abm.domain.MessageSubject;
import edu.wut.thesis.smart_energy_community_abm.domain.TopicHelper;
import jade.core.AID;
import jade.core.ServiceException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class MessageHandlerBehaviour extends BaseMessageHandlerBehaviour {
    private final AID topic;
    protected ApplianceAgent agent;

    public MessageHandlerBehaviour(ApplianceAgent agent) {
        super(agent);
        this.agent = agent;

        try {
            topic = TopicHelper.getTopic(agent, agent.coordinatorName);
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void handleInform(ACLMessage msg) {
        handleHealthCheck(msg);
    }

    private void handleHealthCheck(ACLMessage msg) {
        MessageTemplate healthCheckTemplate = MessageTemplate.MatchTopic(topic);

        if (healthCheckTemplate.match(msg)) {
            ACLMessage reply = msg.createReply(ACLMessage.CONFIRM);
            reply.setOntology(MessageSubject.APPLIANCE_HEALTH_CHECK);
            agent.send(reply);
            agent.log("Confirming check to" + agent.coordinatorName, LogSeverity.INFO);
        }
    }
}
