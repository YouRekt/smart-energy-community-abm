package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase1;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.SpecificMessageTemplateHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.LogSeverity;
import edu.wut.thesis.smart_energy_community_abm.domain.TopicHelper;
import jade.core.AID;
import jade.core.ServiceException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Date;

public class HealthStatusBehaviour extends SpecificMessageTemplateHandlerBehaviour {
    private static final long SUICIDE_TIMEOUT = 1_000;
    private final HouseholdCoordinatorAgent agent;
    private final AID topic;
    private boolean tickReceived = false;
    private long deadline;

    public HealthStatusBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;

        try {
            topic = TopicHelper.getTopic(agent, "TICK");
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        deadline = System.currentTimeMillis() + SUICIDE_TIMEOUT;
    }

    @Override
    protected void handleMatchingMessage(ACLMessage msg) {
        if (msg.getReplyByDate().before(new Date())) {
            agent.log("Failed the health check: read the message too late, killing myself ┗( T﹏T )┛", LogSeverity.ERROR);
            agent.doDelete();
            return;
        }

        final ACLMessage reply = msg.createReply(ACLMessage.CONFIRM);
        agent.send(reply);
        tickReceived = true;
        agent.healthyAppliances.clear();
    }

    @Override
    protected void handleBlock() {
        if (System.currentTimeMillis() >= deadline) {
            agent.log("Failed the health check: past suicide deadline, killing myself ┗( T﹏T )┛", LogSeverity.ERROR);
            agent.doDelete();
            return;
        }

        block(deadline - System.currentTimeMillis());
    }

    @Override
    public boolean done() {
        return tickReceived;
    }

    @Override
    public void reset() {
        super.reset();
        tickReceived = false;
        deadline = System.currentTimeMillis() + SUICIDE_TIMEOUT;
    }

    protected MessageTemplate getMessageTemplate() {
        return MessageTemplate.and(MessageTemplate.MatchTopic(topic), MessageTemplate.MatchPerformative(ACLMessage.INFORM));
    }
}
