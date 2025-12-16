package edu.wut.thesis.smart_energy_community_abm.agents;

import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.SimulationTickBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.ApplianceTask;
import edu.wut.thesis.smart_energy_community_abm.domain.ApplianceTaskInstance;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import edu.wut.thesis.smart_energy_community_abm.domain.messages.TopicHelper;
import jade.core.ServiceException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class ApplianceAgent extends BaseAgent {
    public final Map<ApplianceTask, Long> taskSchedule = new HashMap<>();  // task → lastRunTick
    public final TreeMap<Long, ApplianceTaskInstance> acceptedTasks = new TreeMap<>();  // startTick → instance
    public long tick;
    public boolean insufficientEnergy = false;
    public List<ApplianceTask> tasks;  // from config

    @Override
    protected void setup() {
        super.setup();

        final Object[] args = getArguments();

        final String coordinatorName = (String) args[0];

        if (coordinatorName == null) {
            log("Household Coordinator's name is missing", LogSeverity.ERROR, this);
            doDelete();
            throw new RuntimeException("Household Coordinator's name is missing");
        }

        try {
            TopicHelper.registerTopic(this, coordinatorName);
        } catch (final ServiceException e) {
            throw new RuntimeException(e);
        }

        addBehaviour(new SimulationTickBehaviour(this));
    }

    public boolean shouldTaskRun(ApplianceTask task, long currentTick) {
        long lastRun = taskSchedule.getOrDefault(task, 0L);
        boolean periodElapsed = (currentTick - lastRun) >= task.period();
        boolean humanTriggered = Math.random() < task.humanActivationChance();
        return periodElapsed || humanTriggered;
    }
}
