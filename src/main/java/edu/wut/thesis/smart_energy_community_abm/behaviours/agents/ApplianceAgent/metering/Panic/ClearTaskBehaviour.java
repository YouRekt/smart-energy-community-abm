package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.metering.Panic;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.ApplianceTaskInstance;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Metering.Panic.ACCEPTED_PROPOSAL;

public final class ClearTaskBehaviour extends OneShotBehaviour {
    private final ApplianceAgent agent;

    public ClearTaskBehaviour(ApplianceAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void action() {
        final ACLMessage msg = (ACLMessage) getDataStore().get(ACCEPTED_PROPOSAL);
        ApplianceTaskInstance instance = agent.timetable.get(agent.tick);
        agent.log("Task " + instance.task().taskName() + " (ID: " + instance.task().taskId() + ") interrupted at tick " + agent.tick, LogSeverity.INFO, this);
        agent.clearCurrentTask();
    }
}
