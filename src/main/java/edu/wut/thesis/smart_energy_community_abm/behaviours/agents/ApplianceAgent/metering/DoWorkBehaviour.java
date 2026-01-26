package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.metering;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.ApplianceTaskInstance;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
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
        final ACLMessage msg = (ACLMessage) getDataStore().get(ALLOWED_GREEN_ENERGY);
        final double availableGreenEnergy = Double.parseDouble(msg.getContent());
        final double energyNeeded = agent.getCurrentEnergyUsage();
        double usedGreenEnergy = Math.min(availableGreenEnergy, energyNeeded);
        double usedGridEnergy = energyNeeded - usedGreenEnergy;

        ApplianceTaskInstance instance = agent.timetable.get(agent.tick);
        if (instance != null) {
            agent.taskSchedule.put(agent.timetable.get(agent.tick).task(), agent.tick);
            agent.pushConsumedEnergy(usedGreenEnergy, usedGridEnergy);
            if (instance.endTick() == agent.tick) {
                agent.log("Task " + instance.task().taskName() + " (ID: " + instance.task().taskId() + ") finished at tick " + agent.tick, LogSeverity.INFO, this);
                agent.pushTaskFinished();
            }
        }

        final ACLMessage reply = msg.createReply(ACLMessage.INFORM);
        reply.setOntology(ApplianceAgent.class.getSimpleName());
        // (Green Energy, Grid Energy)
        reply.setContent(String.format("%s,%s", usedGreenEnergy, usedGridEnergy));
        agent.send(reply);
    }
}
