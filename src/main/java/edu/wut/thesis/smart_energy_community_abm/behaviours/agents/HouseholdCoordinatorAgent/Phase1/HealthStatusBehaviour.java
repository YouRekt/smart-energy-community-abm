package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase1;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class HealthStatusBehaviour extends OneShotBehaviour {
    private final HouseholdCoordinatorAgent agent;
    private final ACLMessage msg;

    public HealthStatusBehaviour(HouseholdCoordinatorAgent agent, ACLMessage msg) {
        super(agent);
        this.agent = agent;
        this.msg = msg;
    }

    @Override
    public void action() {
        final ACLMessage reply = msg.createReply(ACLMessage.CONFIRM);
        agent.send(reply);
        agent.healthyAppliances.clear();
    }
}
