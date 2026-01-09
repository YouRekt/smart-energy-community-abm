package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.metering.Panic;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.metering.Panic.CollectPostponeResponseBehaviour.ACCEPTED_PROPOSAL;

public class ClearTaskBehaviour extends OneShotBehaviour {
    private final ApplianceAgent agent;

    public ClearTaskBehaviour(ApplianceAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void action() {
        final ACLMessage msg = (ACLMessage) getDataStore().get(ACCEPTED_PROPOSAL);

        agent.clearCurrentTask();

        final ACLMessage reply = msg.createReply(ACLMessage.INFORM);
        reply.setContent("Task cleared");

        agent.send(reply);
    }
}
