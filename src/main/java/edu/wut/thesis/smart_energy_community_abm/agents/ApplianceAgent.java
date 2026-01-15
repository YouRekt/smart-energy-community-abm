package edu.wut.thesis.smart_energy_community_abm.agents;

import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.SimulationTickBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.ApplianceTask;
import edu.wut.thesis.smart_energy_community_abm.domain.ApplianceTaskInstance;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import edu.wut.thesis.smart_energy_community_abm.domain.messages.TopicHelper;
import jade.core.ServiceException;

import java.util.*;

public final class ApplianceAgent extends BaseAgent {
    public final static int MAX_FUTURE_TICKS = 200;

    public final Map<ApplianceTask, Long> taskSchedule = new HashMap<>();  // task → lastRunTick
    public final TreeMap<Long, ApplianceTaskInstance> timetable = new TreeMap<>();  // startTick → instance
    public long tick;
    public boolean insufficientEnergy = false;
    private List<ApplianceTask> tasks = new ArrayList<>();  // from config

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
        tasks.forEach(task -> taskSchedule.put(task, Long.MIN_VALUE));

        try {
            TopicHelper.registerTopic(this, coordinatorName);
        } catch (final ServiceException e) {
            throw new RuntimeException(e);
        }

        addBehaviour(new SimulationTickBehaviour(this));
    }

    public boolean shouldTaskRun(ApplianceTask task, long currentTick) {
        long lastRun = taskSchedule.getOrDefault(task, Long.MIN_VALUE);
        boolean periodElapsed = (currentTick - lastRun) >= task.period();
        boolean humanTriggered = Math.random() < task.humanActivationChance();
        return periodElapsed || humanTriggered;
    }

    public boolean isRunning() {
        return timetable.get(tick) != null;
    }

    public void clearCurrentTask() {
        var endTick = timetable.get(tick).endTick();

        for (long t = tick; t <= endTick; t++) {
            timetable.remove(t);
        }
    }

    public long getAvailableGapDuration(long fromTick) {
        Map.Entry<Long, ApplianceTaskInstance> floorEntry = timetable.floorEntry(fromTick);
        if (floorEntry != null) {
            ApplianceTaskInstance prevTask = floorEntry.getValue();
            // If the previous task ends at or after fromTick, we are blocked.
            // endTick() returns inclusive end. So if endTick == fromTick, it is occupied.
            if (prevTask.endTick() >= fromTick) {
                return 0;
            }
        }

        Long nextTaskStart = timetable.higherKey(fromTick);

        if (nextTaskStart == null) {
            // No future tasks, space is effectively infinite
            return Long.MAX_VALUE;
        } else {
            return nextTaskStart - fromTick;
        }
    }

    public double getCurrentEnergyUsage() {
        ApplianceTaskInstance instance = timetable.get(tick);

        if (instance != null) {
            return instance.task().energyPerTick();
        }

        return 0.0;
    }
}
