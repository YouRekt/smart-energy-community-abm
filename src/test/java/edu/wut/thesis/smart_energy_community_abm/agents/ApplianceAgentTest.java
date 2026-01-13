package edu.wut.thesis.smart_energy_community_abm.agents;

import edu.wut.thesis.smart_energy_community_abm.domain.ApplianceTask;
import edu.wut.thesis.smart_energy_community_abm.domain.ApplianceTaskInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApplianceAgentTest {

    private ApplianceAgent agent;

    @BeforeEach
    void setUp() {
        agent = new ApplianceAgent();
    }

    private ApplianceTask createTask(int period, double chance) {
        // Name, Chance, Period, Postponable, Duration, Energy
        return new ApplianceTask("TestAppliance", chance, period, true, 5, 10.0);
    }

    @Test
    @DisplayName("shouldTaskRun: Should return TRUE if period has elapsed (Random disabled)")
    void testShouldTaskRunPeriodElapsed() {
        ApplianceTask task = createTask(10, 0.0); // 0% random chance
        agent.taskSchedule.put(task, 0L); // Last run at tick 0

        // Check at tick 10 (10 - 0 >= 10)
        assertTrue(agent.shouldTaskRun(task, 10), "Task should run when period elapsed");
    }

    @Test
    @DisplayName("shouldTaskRun: Should return FALSE if period has NOT elapsed (Random disabled)")
    void testShouldTaskRunWait() {
        ApplianceTask task = createTask(10, 0.0);
        agent.taskSchedule.put(task, 0L);

        // Check at tick 9
        assertFalse(agent.shouldTaskRun(task, 9), "Task should wait for period");
    }

    @Test
    @DisplayName("shouldTaskRun: Should return TRUE if Human Activation triggers (Force 100%)")
    void testShouldTaskRunRandomTrigger() {
        ApplianceTask task = createTask(10, 2.0); // >100% chance
        agent.taskSchedule.put(task, 0L);

        // Check at tick 1 (Period not elapsed, but human triggered)
        assertTrue(agent.shouldTaskRun(task, 1), "Human activation should override period wait");
    }

    @Test
    @DisplayName("clearCurrentTask: Should remove task instance from all occupied ticks")
    void testClearCurrentTask() {
        // Given a task running from tick 10 to 14 (Duration 5)
        ApplianceTask task = createTask(10, 0.0);
        ApplianceTaskInstance instance = new ApplianceTaskInstance(task, 10, 5);

        // Populate timetable manually
        for (long t = 10; t < 15; t++) {
            agent.timetable.put(t, instance);
        }

        // When agent is at tick 10 and clears the task
        agent.tick = 10;
        agent.clearCurrentTask();

        // Then
        assertNull(agent.timetable.get(10L));
        assertNull(agent.timetable.get(14L));
        assertTrue(agent.timetable.isEmpty(), "Timetable should be empty after clearing");
    }
}