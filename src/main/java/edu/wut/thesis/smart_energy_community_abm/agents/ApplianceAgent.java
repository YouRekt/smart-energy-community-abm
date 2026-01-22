package edu.wut.thesis.smart_energy_community_abm.agents;

import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.SimulationTickBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.ApplianceTask;
import edu.wut.thesis.smart_energy_community_abm.domain.ApplianceTaskInstance;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import edu.wut.thesis.smart_energy_community_abm.domain.messages.TopicHelper;
import edu.wut.thesis.smart_energy_community_abm.domain.util.MetricNameHelper;
import jade.core.ServiceException;

import java.util.*;

public final class ApplianceAgent extends BaseAgent {
    public final static int MAX_FUTURE_TICKS = 200;

    public final Map<ApplianceTask, Long> taskSchedule = new HashMap<>();  // task → lastRunTick
    public final TreeMap<Long, ApplianceTaskInstance> timetable = new TreeMap<>();  // startTick → instance
    private final List<ApplianceTask> tasks = new ArrayList<>();  // from config
    private String householdName;

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

        if (configTasks == null || configTasks.isEmpty()) {
            log("Task list missing or empty", LogSeverity.ERROR, this);
            doDelete();
            throw new RuntimeException("Task list missing or empty");
        }

        tasks.addAll(configTasks);
        tasks.forEach(task -> taskSchedule.put(task, rand.nextLong(MAX_FUTURE_TICKS)));
//        tasks.forEach(task -> taskSchedule.put(task, 4L));

        householdName = coordinatorName;

        try {
            TopicHelper.registerTopic(this, coordinatorName);
        } catch (final ServiceException e) {
            throw new RuntimeException(e);
        }

        addBehaviour(new SimulationTickBehaviour(this));
    }

    public boolean shouldTaskRun(ApplianceTask task, long currentTick) {
        long lastRun = taskSchedule.getOrDefault(task, Long.MIN_VALUE);

        // 1. If never run, it is due immediately
        if (lastRun == Long.MIN_VALUE) {
            return true;
        }

        // 2. Check if period has elapsed
        boolean periodElapsed = (currentTick - lastRun) >= task.period();
        if (periodElapsed) {
            return true;
        }

        // 3. If not strictly due, check random human activation
        return rand.nextDouble() < task.humanActivationChance();
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

    public long findFirstAvailableSlot(ApplianceTask task) {
        long currentSearchTick = tick + 1;

        // Limit search horizon to avoid infinite loops if the schedule is packed forever (unlikely but safe)
        long searchLimit = tick + Math.max(task.period(), MAX_FUTURE_TICKS);

        while (currentSearchTick < searchLimit) {
            long gap = getAvailableGapDuration(currentSearchTick);

            if (gap >= task.duration()) {
                return currentSearchTick;
            }

            // Optimization: If gap is 0 (occupied), jump to the end of the blocking task.
            if (gap == 0) {
                // Find next key (start of next task) isn't helpful if we are *inside* a task.
                // We need the END of the current blocking task.
                var entry = timetable.floorEntry(currentSearchTick);
                if (entry != null && entry.getValue().endTick() >= currentSearchTick) {
                    currentSearchTick = entry.getValue().endTick() + 1;
                } else {
                    // Should theoretically not happen if gap returns 0 logic is correct
                    currentSearchTick++;
                }
            } else {
                // We found a gap, but it was too small.
                // The gap ends at (currentSearchTick + gap).
                // The next task starts immediately after.
                // Safest is to jump to the start of the next task (which defines the end of this gap).
                Long nextTaskStart = timetable.higherKey(currentSearchTick);
                if (nextTaskStart != null) {
                    // The gap ends at nextTaskStart. The task at nextTaskStart is blocking us.
                    // We need to jump past that task.
                    currentSearchTick = timetable.get(nextTaskStart).endTick() + 1;
                } else {
                    // This implies gap is MAX_LONG, so we should have returned already.
                    currentSearchTick++;
                }
            }
        }
        return -1;
    }

    public void pushConsumedEnergy(double greenEnergy, double gridEnergy) {
        pushMetric(MetricNameHelper.applianceGreenConsumption(householdName, getLocalName()), greenEnergy);
        pushMetric(MetricNameHelper.applianceGridConsumption(householdName, getLocalName()), gridEnergy);
    }
}
