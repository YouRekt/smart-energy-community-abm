package edu.wut.thesis.smart_energy_community_abm.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AllocationEntryTest {

    @Test
    @DisplayName("allocationEnd() should calculate inclusive end tick correctly")
    void testAllocationEnd() {
        // Given
        long allocationStart = 200;
        long duration = 20;
        // Other fields (energy, requestTimestamp) don't affect this calculation
        AllocationEntry entry = new AllocationEntry(500.0, allocationStart, duration);

        // When
        long endTick = entry.allocationEnd();

        // Then
        // 200 + 20 - 1 = 219
        assertEquals(219, endTick);
    }

    @Test
    @DisplayName("Should handle duration of 1 correctly")
    void testSingleTickDuration() {
        // Given
        long start = 100;
        long duration = 1;
        AllocationEntry entry = new AllocationEntry(10.0, start, duration);

        // When
        long end = entry.allocationEnd();

        // Then
        assertEquals(start, end, "For duration 1, start and end tick should be the same");
    }
}