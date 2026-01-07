package edu.wut.thesis.smart_energy_community_abm.agents;

import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.SimulationTickBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.ApplianceTask;
import edu.wut.thesis.smart_energy_community_abm.domain.ApplianceTaskInstance;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import edu.wut.thesis.smart_energy_community_abm.domain.messages.TopicHelper;
import jade.core.ServiceException;

import java.util.*;

public final class ApplianceAgent extends BaseAgent {
    public final Map<ApplianceTask, Long> taskSchedule = new HashMap<>();  // task → lastRunTick
    public final TreeMap<Long, ApplianceTaskInstance> acceptedTasks = new TreeMap<>();  // startTick → instance
    public long tick;
    public boolean insufficientEnergy = false;
    public List<ApplianceTask> tasks = new ArrayList<>();  // from config

    @SuppressWarnings("unchecked")
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

        final List<ApplianceTask> configTasks = (List<ApplianceTask>) args[1];

        if  (configTasks == null || configTasks.isEmpty()) {
            log("Task list missing or empty", LogSeverity.ERROR, this);
            doDelete();
            throw new RuntimeException("Task list missing or empty");
        }

        tasks.addAll(configTasks);
        // TODO: Maybe add some delay so all tasks don't schedule at once?
        tasks.forEach(task -> taskSchedule.put(task, 0L));

        try {
            TopicHelper.registerTopic(this, coordinatorName);
        } catch (final ServiceException e) {
            throw new RuntimeException(e);
        }

        addBehaviour(new SimulationTickBehaviour(this));
    }

    // TODO: Call this in Phase 3
    public boolean shouldTaskRun(ApplianceTask task, long currentTick) {
        long lastRun = taskSchedule.getOrDefault(task, 0L);
        boolean periodElapsed = (currentTick - lastRun) >= task.period();
        boolean humanTriggered = Math.random() < task.humanActivationChance();
        return periodElapsed || humanTriggered;
    }
}
