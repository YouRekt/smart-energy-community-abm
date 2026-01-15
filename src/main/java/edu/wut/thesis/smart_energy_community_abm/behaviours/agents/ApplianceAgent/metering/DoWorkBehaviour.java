package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.metering;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Metering.ALLOWED_GREEN_ENERGY;

public final class DoWorkBehaviour extends OneShotBehaviour {
    private final ApplianceAgent agent;

    public DoWorkBehaviour(ApplianceAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void action() {
        // TODO: Do work and push metrics to database
        final ACLMessage msg = (ACLMessage) getDataStore().get(ALLOWED_GREEN_ENERGY);
        final double availableGreenEnergy = Double.parseDouble(msg.getContent());
        double energyUsedThisTick = agent.getCurrentEnergyUsage();
        double usedGreenEnergy = Math.min(availableGreenEnergy, energyUsedThisTick);
        double usedGridEnergy = energyUsedThisTick - usedGreenEnergy;

        final ACLMessage reply = msg.createReply(ACLMessage.INFORM);
        reply.setOntology(ApplianceAgent.class.getSimpleName());
        // (Green Energy, Grid Energy)
        reply.setContent(String.format("%s,%s", usedGreenEnergy, usedGridEnergy));
        agent.send(reply);
    }
}
