package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase1;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Date;

public class ProcessApplianceHealthStatusBehaviour extends OneShotBehaviour {
    private final HouseholdCoordinatorAgent agent;
    private final ACLMessage msg;

    public ProcessApplianceHealthStatusBehaviour(HouseholdCoordinatorAgent agent, ACLMessage msg) {
        super(agent);
        this.agent = agent;
        this.msg = msg;
    }

    @Override
    public void action() {
        Date replyBy = (Date) getDataStore().get(ApplianceTickRelayBehaviour.HEALTH_REPLY_BY);

        if (replyBy.after(new Date())) {
            agent.healthyAppliances.add(msg.getSender());
        }

        if (agent.healthyAppliances.size() == agent.applianceCount) {
            agent.phase++;
        }
    }

    protected MessageTemplate getMessageTemplate() {
        return MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.CONFIRM), MessageTemplate.MatchOntology(agent.name));
    }
}
