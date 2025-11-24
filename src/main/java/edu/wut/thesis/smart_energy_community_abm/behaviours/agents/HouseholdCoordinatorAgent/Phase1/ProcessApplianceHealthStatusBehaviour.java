package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase1;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.SpecificMessageTemplateHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.LogSeverity;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Date;

public class ProcessApplianceHealthStatusBehaviour extends SpecificMessageTemplateHandlerBehaviour {
    private final HouseholdCoordinatorAgent agent;

    public ProcessApplianceHealthStatusBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    protected void handleBlock() {
        if (agent.healthyAppliances.size() == agent.applianceCount) {
            return;
        }

        Date replyBy = (Date) getDataStore().get(ApplianceTickRelayBehaviour.HEALTH_REPLY_BY);

        agent.log("Blocking for " + (replyBy.getTime() - System.currentTimeMillis()), LogSeverity.INFO);

        block(replyBy.getTime() - System.currentTimeMillis());
    }

    @Override
    protected void handleMatchingMessage(ACLMessage msg) {
        Date replyBy = (Date) getDataStore().get(ApplianceTickRelayBehaviour.HEALTH_REPLY_BY);

        if (replyBy.after(new Date())) {
            agent.healthyAppliances.add(msg.getSender());
        }
    }

    protected MessageTemplate getMessageTemplate() {
        return MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.CONFIRM), MessageTemplate.MatchOntology(agent.name));
    }


    @Override
    public boolean done() {
        Date replyBy = (Date) getDataStore().get(ApplianceTickRelayBehaviour.HEALTH_REPLY_BY);

        final boolean flag = replyBy.before(new Date());

        agent.log("DONE: " + (flag ? "true" : "false"), LogSeverity.INFO);

        return flag || agent.healthyAppliances.size() == agent.applianceCount;
    }
}
