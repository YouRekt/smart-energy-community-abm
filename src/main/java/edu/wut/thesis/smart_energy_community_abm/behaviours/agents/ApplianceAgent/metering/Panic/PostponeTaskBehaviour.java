package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.metering.Panic;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.ApplianceTask;
import jade.core.behaviours.OneShotBehaviour;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Metering.Panic.TASK_TO_POSTPONE;

public final class PostponeTaskBehaviour extends OneShotBehaviour {
    final private ApplianceAgent agent;

    PostponeTaskBehaviour(ApplianceAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        final ApplianceTask taskToPostpone = (ApplianceTask) getDataStore().get(TASK_TO_POSTPONE);
        agent.taskSchedule.put(taskToPostpone, agent.taskSchedule.get(taskToPostpone) + taskToPostpone.period());
    }
}
