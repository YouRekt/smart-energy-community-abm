package edu.wut.thesis.smart_energy_community_abm.domain.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class BatteryConfigTest {

    @Test
    @DisplayName("Should create config with valid values (isPercentage=true)")
    void testValidConfigTrue() {
        assertDoesNotThrow(() -> new BatteryConfig(1000.0, 0.5, true));
    }

    @Test
    @DisplayName("Should create config with valid values (isPercentage=false)")
    void testValidConfigFalse() {
        // This was previously invalid, now checks that it passes
        assertDoesNotThrow(() -> new BatteryConfig(1000.0, 0.5, false));
    }

    @Test
    @DisplayName("Should throw if capacity is null or negative")
    void testInvalidCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new BatteryConfig(null, 0.5, true));
        assertThrows(IllegalArgumentException.class, () -> new BatteryConfig(-100.0, 0.5, true));
        assertThrows(IllegalArgumentException.class, () -> new BatteryConfig(0.0, 0.5, true));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.1, 0.0, 1.1})
    @DisplayName("Should throw if startingCharge is not within (0, 1]")
    void testInvalidStartingCharge(double invalidCharge) {
        // Valid input is always a ratio 0.0 < x <= 1.0
        assertThrows(IllegalArgumentException.class, () -> new BatteryConfig(1000.0, invalidCharge, true));
    }

    @Test
    @DisplayName("Should throw if isPercentage is null")
    void testIsPercentageNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new BatteryConfig(1000.0, 0.5, null));

        assertTrue(ex.getMessage().contains("percentage argument cannot be null"),
                "Exception message should mention the null percentage argument");
    }

    @Test
    @DisplayName("getAgentParams should calculate charge based on flag")
    void testGetAgentParams() {
        double capacity = 2000.0;
        double ratio = 0.5;

        // Case 1: isPercentage = TRUE
        // The agent receives the raw ratio (0.5)
        BatteryConfig configPercentage = new BatteryConfig(capacity, ratio, true);
        Object[] argsPercentage = configPercentage.getAgentParams().agentArgs();

        assertEquals(capacity, argsPercentage[0]);
        assertEquals(ratio, argsPercentage[1]);

        // Case 2: isPercentage = FALSE
        // The agent receives the calculated absolute value (0.5 * 2000 = 1000)
        BatteryConfig configAbsolute = new BatteryConfig(capacity, ratio, false);
        Object[] argsAbsolute = configAbsolute.getAgentParams().agentArgs();

        assertEquals(capacity, argsAbsolute[0]);
        assertEquals(ratio * capacity, argsAbsolute[1]);
    }
}