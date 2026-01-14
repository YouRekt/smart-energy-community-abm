package edu.wut.thesis.smart_energy_community_abm.domain;

import jade.core.AID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class EnergyRequestTest {

    @Test
    @DisplayName("endTick() should return the last inclusive tick (start + duration - 1)")
    void testEndTick() {
        // Given
        long startTick = 100;
        int duration = 10;
        EnergyRequest request = new EnergyRequest(new AID("Appliance1@local-test", AID.ISGUID), startTick, duration, 50.0);

        // When
        long endTick = request.endTick();

        // Then
        // 100 + 10 - 1 = 109
        assertEquals(109, endTick, "End tick should be inclusive of the duration");
    }

    @ParameterizedTest(name = "Tick {0} should be active: {1} (Start: 10, Duration: 5)")
    @CsvSource({
            "9,  false", // Before start
            "10, true",  // Start tick
            "12, true",  // Middle
            "14, true",  // Last tick (inclusive) -> 10 + 5 - 1 = 14
            "15, false"  // Immediately after duration
    })
    @DisplayName("isActive() boundary analysis")
    void testIsActive(long tick, boolean expectedActive) {
        // Given
        long startTick = 10;
        int duration = 5; // Ticks: 10, 11, 12, 13, 14
        EnergyRequest request = new EnergyRequest(new AID("Appliance1@local-test", AID.ISGUID), startTick, duration, 20.0);

        // When
        boolean isActive = request.isActive(tick);

        // Then
        assertEquals(expectedActive, isActive,
                () -> String.format("Tick %d should be %s", tick, expectedActive ? "active" : "inactive"));
    }

    @Test
    @DisplayName("Should run exactly for the specified duration count")
    void testDurationConsistency() {
        // Given
        long startTick = 50;
        int duration = 3;
        EnergyRequest request = new EnergyRequest(new AID("Appliance1@local-test", AID.ISGUID), startTick, duration, 10.0);

        // When
        int activeCount = 0;
        for (long i = startTick - 1; i <= startTick + duration + 1; i++) {
            if (request.isActive(i)) {
                activeCount++;
            }
        }

        // Then
        assertEquals(duration, activeCount, "Request should be active for exactly 'duration' ticks");
    }
}