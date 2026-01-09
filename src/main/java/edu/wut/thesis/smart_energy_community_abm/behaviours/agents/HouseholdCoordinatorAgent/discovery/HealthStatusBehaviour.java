package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.discovery;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public final class HealthStatusBehaviour extends OneShotBehaviour {
    private final HouseholdCoordinatorAgent agent;

    public HealthStatusBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void action() {
        final ACLMessage msg = (ACLMessage) getDataStore().get(DataStoreKey.Discovery.TICK_MSG);
        final ACLMessage reply = msg.createReply(ACLMessage.CONFIRM);
        reply.setOntology(HouseholdCoordinatorAgent.class.getSimpleName());
        agent.send(reply);
    }
}
