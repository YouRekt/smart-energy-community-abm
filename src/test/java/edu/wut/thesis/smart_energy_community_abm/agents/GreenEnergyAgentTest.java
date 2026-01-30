package edu.wut.thesis.smart_energy_community_abm.agents;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class GreenEnergyAgentTest {

    private GreenEnergyAgent agent;

    // Simulation Constants
    private static final long PERIOD = 100L;
    private static final double MAX_POWER = 1000.0;
    private static final long PEAK_TICK = 50L; // Peak in the middle
    private static final double STD_DEV = 10.0;
    private static final double VARIATION = 0.1;

    @BeforeEach
    void setUp() {
        agent = new GreenEnergyAgent();
        // Inject configuration using Reflection to bypass JADE setup()
        setPrivateField("period", PERIOD);
        setPrivateField("maxOutputPower", MAX_POWER);
        setPrivateField("peakTick", PEAK_TICK);
        setPrivateField("stdDev", STD_DEV);
        setPrivateField("variation", VARIATION);
    }

    private void setPrivateField(String fieldName, Object value) {
        try {
            Field field = GreenEnergyAgent.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(agent, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject field: " + fieldName, e);
        }
    }

    @Test
    @DisplayName("Should produce Max Power exactly at the Peak Tick (Noise Off)")
    void testPeakProduction() {
        // Given
        agent.tick = PEAK_TICK;

        // When
        Double production = agent.produceEnergy(false);

        // Then
        // exp(0) = 1.0 -> should be exactly max power
        assertEquals(MAX_POWER, production, 0.001);
    }

    @Test
    @DisplayName("Should produce symmetric output around the peak (Gaussian property)")
    void testSymmetry() {
        // Given
        long offset = 5;
        long leftTick = PEAK_TICK - offset;  // 45
        long rightTick = PEAK_TICK + offset; // 55

        // When
        agent.tick = leftTick;
        Double leftProd = agent.produceEnergy(false);

        agent.tick = rightTick;
        Double rightProd = agent.produceEnergy(false);

        // Then
        assertEquals(leftProd, rightProd, 0.001, "Gaussian curve should be symmetric");
        assertTrue(leftProd < MAX_POWER, "Production away from peak should be lower than max");
    }

    @Test
    @DisplayName("Should handle periodic wraparound correctly")
    void testPeriodicWraparound() {
        // Scenario: Peak is at 50. Period is 100.
        // Tick 0 and Tick 100 should be the same distance from peak (50 units away).
        // Tick 150 (modulo 100 = 50) should be a peak.

        // Case 1: Wraparound Peak
        agent.tick = PERIOD + PEAK_TICK; // 150
        assertEquals(MAX_POWER, agent.produceEnergy(false), 0.001);

        // Case 2: Boundary Distance
        // Peak is 50.
        // Tick 10 (dist 40).
        // Tick 90 (dist 40? |90-50|=40).
        agent.tick = 10;
        Double prod10 = agent.produceEnergy(false);

        agent.tick = 90;
        Double prod90 = agent.produceEnergy(false);

        assertEquals(prod10, prod90, 0.001, "Distance calculation should handle periodic boundaries");
    }

    @ParameterizedTest
    @CsvSource({
            "50, 1.0",     // Peak -> 100%
            "40, 0.606",   // 1 std dev away -> exp(-0.5 * 1^2) ~= 0.606
            "30, 0.135",   // 2 std dev away -> exp(-0.5 * 2^2) = exp(-2) ~= 0.135
            "20, 0.011"    // 3 std dev away -> exp(-4.5) ~= 0.011
    })
    @DisplayName("Should follow Gaussian Decay")
    void testGaussianValues(long tick, double expectedRatio) {
        agent.tick = tick;
        Double production = agent.produceEnergy(false);

        double expected = MAX_POWER * expectedRatio;
        // Allow slight delta for floating point math
        assertEquals(expected, production, 1.0);
    }

    @Test
    @DisplayName("Random noise should affect output but stay non-negative")
    void testRandomNoise() {
        agent.tick = PEAK_TICK;

        boolean different = false;
        for (int i = 0; i < 100; i++) {
            Double noisyProduction = agent.produceEnergy(true);

            // Check 1: Non-negative
            assertTrue(noisyProduction >= 0.0, "Energy production cannot be negative");

            // Check 2: It actually varies (extremely unlikely to hit exactly 1000.0 100 times in a row)
            if (Math.abs(noisyProduction - MAX_POWER) > 0.0001) {
                different = true;
            }
        }

        assertTrue(different, "Random noise should produce different values over time");
    }
}