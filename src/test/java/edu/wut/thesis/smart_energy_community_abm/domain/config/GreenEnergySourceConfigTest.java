package edu.wut.thesis.smart_energy_community_abm.domain.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class GreenEnergySourceConfigTest {

    // Helper to create a valid config builder
    private GreenEnergySourceConfig createConfig(Long period, Double maxPower, Long peakTick, Double stdDev, Double variation, String name) {
        return new GreenEnergySourceConfig(period, maxPower, peakTick, stdDev, variation, name);
    }

    // Default valid values for use in tests where we only want to break one parameter
    private static final Long VALID_PERIOD = 100L;
    private static final Double VALID_MAX_POWER = 5000.0;
    private static final Long VALID_PEAK_TICK = 50L;
    private static final Double VALID_STD_DEV = 10.0;
    private static final Double VALID_VARIATION = 0.1;
    private static final String VALID_NAME = "GreenSource1";

    @Test
    @DisplayName("Should create config successfully with valid parameters")
    void testValidCreation() {
        assertDoesNotThrow(() -> new GreenEnergySourceConfig(
                VALID_PERIOD, VALID_MAX_POWER, VALID_PEAK_TICK, VALID_STD_DEV, VALID_VARIATION, VALID_NAME
        ));
    }

    @ParameterizedTest(name = "Period {0} should be invalid")
    @NullSource
    @ValueSource(longs = {0L, -100L})
    @DisplayName("Should throw exception for invalid Period")
    void testInvalidPeriod(Long invalidPeriod) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                createConfig(invalidPeriod, VALID_MAX_POWER, VALID_PEAK_TICK, VALID_STD_DEV, VALID_VARIATION, VALID_NAME)
        );
        assertTrue(ex.getMessage().contains("period"), "Exception message should mention 'period'");
    }

    @ParameterizedTest(name = "MaxOutputPower {0} should be invalid")
    @NullSource
    @ValueSource(doubles = {0.0, -50.0})
    @DisplayName("Should throw exception for invalid MaxOutputPower")
    void testInvalidMaxOutputPower(Double invalidPower) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                createConfig(VALID_PERIOD, invalidPower, VALID_PEAK_TICK, VALID_STD_DEV, VALID_VARIATION, VALID_NAME)
        );
        assertTrue(ex.getMessage().contains("maxOutputPower"), "Exception message should mention 'maxOutputPower'");
    }

    @ParameterizedTest(name = "StdDev {0} should be invalid")
    @NullSource
    @ValueSource(doubles = {0.0, -1.0})
    @DisplayName("Should throw exception for invalid Standard Deviation")
    void testInvalidStdDev(Double invalidStdDev) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                createConfig(VALID_PERIOD, VALID_MAX_POWER, VALID_PEAK_TICK, invalidStdDev, VALID_VARIATION, VALID_NAME)
        );
        assertTrue(ex.getMessage().contains("stdDev"), "Exception message should mention 'stdDev'");
    }

    @ParameterizedTest(name = "Variation {0} should be invalid")
    @NullSource
    @ValueSource(doubles = {0.0, -0.1, 1.1, 2.0})
    @DisplayName("Should throw exception for Variation outside (0, 1]")
    void testInvalidVariation(Double invalidVariation) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                createConfig(VALID_PERIOD, VALID_MAX_POWER, VALID_PEAK_TICK, VALID_STD_DEV, invalidVariation, VALID_NAME)
        );
        assertTrue(ex.getMessage().contains("variation"), "Exception message should mention 'variation'");
    }

    @ParameterizedTest(name = "AgentName '{0}' should be invalid")
    @NullSource
    @ValueSource(strings = {"", "   ", "\t\n"})
    @DisplayName("Should throw exception for blank Agent Name")
    void testInvalidAgentName(String invalidName) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                createConfig(VALID_PERIOD, VALID_MAX_POWER, VALID_PEAK_TICK, VALID_STD_DEV, VALID_VARIATION, invalidName)
        );
        assertTrue(ex.getMessage().contains("agentName"), "Exception message should mention 'agentName'");
    }
}