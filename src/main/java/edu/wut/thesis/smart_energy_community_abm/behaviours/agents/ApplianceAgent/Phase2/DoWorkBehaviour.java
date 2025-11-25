package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.Phase2;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.Phase2.ProcessEnergyOutcomeBehaviour.ALLOWED_GREEN_ENERGY;

public final class DoWorkBehaviour extends OneShotBehaviour {
    private final ApplianceAgent agent;

    public DoWorkBehaviour(ApplianceAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void action() {
        // TODO: Do work and push metrics
        final ACLMessage msg = (ACLMessage) getDataStore().get(ALLOWED_GREEN_ENERGY);

        final ACLMessage reply = msg.createReply(ACLMessage.INFORM);
        reply.setOntology(ApplianceAgent.class.getSimpleName());
        reply.setContent(String.format("%s,%s", "1000.0", "0.0"));
        agent.send(reply);
    }
}
