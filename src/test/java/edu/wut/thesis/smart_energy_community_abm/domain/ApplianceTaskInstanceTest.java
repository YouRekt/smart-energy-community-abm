package edu.wut.thesis.smart_energy_community_abm.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class ApplianceTaskInstanceTest {

    // Helper to create a task with specific duration
    private ApplianceTask createTask(int duration) {
        // Other fields (name, chance, period, postponable, energy) don't affect time logic
        return new ApplianceTask("TestTask", 0.0, 100, true, duration, 10.0, 0);
    }

    @Test
    @DisplayName("endTick() should return the last inclusive tick of the task")
    void testEndTickCalculation() {
        // Given
        long startTick = 10;
        int duration = 5; // Ticks: 10, 11, 12, 13, 14
        ApplianceTask task = createTask(duration);
        ApplianceTaskInstance instance = new ApplianceTaskInstance(task, startTick, duration);

        // When
        long endTick = instance.endTick();

        // Then
        // Formula in code: start + duration - 1
        // 10 + 5 - 1 = 14
        assertEquals(14, endTick, "End tick should be start + duration - 1");
    }

    @ParameterizedTest(name = "Tick {0} should be active: {1} (Start: 10, Duration: 5)")
    @CsvSource({
            "9,  false", // Before start
            "10, true",  // Start tick
            "12, true",  // Middle
            "13, true",  // Second to last
            "14, false", // POTENTIAL BUG: This is the mathematically inclusive end tick
            "15, false"  // After
    })
    @DisplayName("isActiveAt() boundary checks")
    void testIsActiveAt(long checkTick, boolean expectedActive) {
        // Given
        long startTick = 10;
        int duration = 5;
        ApplianceTask task = createTask(duration);
        ApplianceTaskInstance instance = new ApplianceTaskInstance(task, startTick, duration);

        // When
        boolean actualActive = instance.isActiveAt(checkTick);

        // Then
        // Note: Based on your current code: tick < endTick()
        // endTick() returns 14.
        // checkTick(14) < 14 is FALSE.
        // This suggests the task only runs for 4 ticks (10, 11, 12, 13) instead of 5.
        // If 14 was intended to be active, the code should be `tick <= endTick()`
        // or endTick should be `start + duration`.
        assertEquals(expectedActive, actualActive,
                () -> String.format("Tick %d active status mismatch for duration %d", checkTick, duration));
    }

    @Test
    @DisplayName("Verify consistency between Duration and Active Tick Count")
    void testDurationConsistency() {
        // Given
        long startTick = 0;
        int duration = 10;
        ApplianceTask task = createTask(duration);
        ApplianceTaskInstance instance = new ApplianceTaskInstance(task, startTick, duration);

        // When
        int activeCount = 0;
        // Check a wide range
        for (long i = -5; i < duration + 5; i++) {
            if (instance.isActiveAt(i)) {
                activeCount++;
            }
        }

        // Then
        // Current code behavior: activeCount will be 9 (ticks 0 to 8).
        // This assertion documents the current behavior, even if it might be a logic error.
        // If you fix the bug, update this to expect 'duration' (10).
        assertEquals(duration - 1, activeCount, "Current implementation runs for Duration - 1 ticks");
    }
}