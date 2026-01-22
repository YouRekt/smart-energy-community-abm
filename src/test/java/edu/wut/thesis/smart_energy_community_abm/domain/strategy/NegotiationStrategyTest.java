package edu.wut.thesis.smart_energy_community_abm.domain.strategy;

import edu.wut.thesis.smart_energy_community_abm.domain.PanicContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class NegotiationStrategyTest {

    private PanicContext createPanicContext(double shortfall, double batteryCharge, double minThreshold) {
        return new PanicContext(shortfall, batteryCharge, minThreshold);
    }

    @Nested
    @DisplayName("Balanced Strategy Tests")
    class BalancedStrategyTests {
        private final NegotiationStrategy strategy = new BalancedStrategy();

        @Test
        @DisplayName("Should compute priority using balanced weights (0.5 Green, 0.5 Cooperation)")
        void testComputePriority() {
            // Given
            double greenScore = 0.2;
            double cooperationScore = 0.8;
            long tick = 100; // Irrelevant for Balanced
            long span = 10;  // Irrelevant for Balanced

            // When
            double priority = strategy.computeNegotiationPriority(greenScore, cooperationScore, tick, span);

            // Then
            // Calculation: (0.2 * 0.5) + (0.8 * 0.5) = 0.1 + 0.4 = 0.5
            assertEquals(0.5, priority, 0.0001);
        }

        @Test
        @DisplayName("Should trigger panic when shortfall exceeds usable buffer")
        void testPanicTrigger() {
            // Buffer = 1000 * (1.0 - 0.2) = 800
            // Threshold is simply > buffer for Balanced
            PanicContext safeCtx = createPanicContext(800.0, 1000.0, 0.2);
            PanicContext panicCtx = createPanicContext(800.1, 1000.0, 0.2);

            assertFalse(strategy.shouldTriggerPanic(safeCtx));
            assertTrue(strategy.shouldTriggerPanic(panicCtx));
        }
    }

    @Nested
    @DisplayName("Green Score First Strategy Tests")
    class GreenScoreFirstTests {
        private final NegotiationStrategy strategy = new GreenScoreFirstStrategy();

        @Test
        @DisplayName("Should heavily weigh green score (0.8 Green, 0.2 Cooperation)")
        void testComputePriority() {
            // Given
            double greenScore = 1.0;
            double cooperationScore = 0.0;
            long tick = 100; // Irrelevant
            long span = 10;  // Irrelevant

            // When
            double priority = strategy.computeNegotiationPriority(greenScore, cooperationScore, tick, span);

            // Then
            // Calculation: (1.0 * 0.8) + (0.0 * 0.2) = 0.8
            assertEquals(0.8, priority, 0.0001);
        }

        @Test
        @DisplayName("Should trigger panic at 70% of usable buffer")
        void testPanicTrigger() {
            // Buffer = 1000 * 0.8 = 800
            // Threshold = 800 * 0.7 = 560
            PanicContext safeCtx = createPanicContext(560.0, 1000.0, 0.2);
            PanicContext panicCtx = createPanicContext(560.1, 1000.0, 0.2);

            assertFalse(strategy.shouldTriggerPanic(safeCtx));
            assertTrue(strategy.shouldTriggerPanic(panicCtx));
        }
    }

    @Nested
    @DisplayName("Reservation First Strategy Tests")
    class ReservationFirstTests {
        private final NegotiationStrategy strategy = new ReservationFirstStrategy();

        @Test
        @DisplayName("Should weigh cooperation highly and include tick urgency (0.2 Green, 0.7 Coop, 0.1 Tick)")
        void testComputePriority() {
            // Given
            double greenScore = 0.5;
            double cooperationScore = 1.0;
            long firstTaskTick = 0; // Tick 0 results in max tick factor
            long span = 10;

            // When
            double priority = strategy.computeNegotiationPriority(greenScore, cooperationScore, firstTaskTick, span);

            // Calculation:
            // Green: 0.5 * 0.2 = 0.1
            // Coop:  1.0 * 0.7 = 0.7
            // Tick:  1.0 / (1.0 + ln(1 + 0)) = 1.0 -> 1.0 * 0.1 = 0.1
            // Total: 0.9
            assertEquals(0.9, priority, 0.0001);
        }

        @Test
        @DisplayName("Should be lenient with panic (110% of usable buffer)")
        void testPanicTrigger() {
            // Buffer = 1000 * 0.8 = 800
            // Threshold = 800 * 1.1 = 880
            PanicContext safeCtx = createPanicContext(880.0, 1000.0, 0.2);
            PanicContext panicCtx = createPanicContext(880.1, 1000.0, 0.2);

            assertFalse(strategy.shouldTriggerPanic(safeCtx));
            assertTrue(strategy.shouldTriggerPanic(panicCtx));
        }
    }

    @Nested
    @DisplayName("Energy Volume Strategy Tests")
    class EnergyVolumeTests {
        private final NegotiationStrategy strategy = new EnergyVolumeStrategy();

        @ParameterizedTest
        @CsvSource({
                "0,  1.0",    // Small span (0) -> Factor 1.0
                "10, 0.5"     // Span equals scale (10) -> Factor 0.5
        })
        @DisplayName("Should penalize large request spans (Volume proxy)")
        void testSpanFactor(long span, double expectedSpanFactor) {
            // Given
            double greenScore = 0.0;
            double cooperationScore = 0.0;
            long tick = 100;

            // When
            double priority = strategy.computeNegotiationPriority(greenScore, cooperationScore, tick, span);

            // Formula weights: Green 0.3, Coop 0.3, Span 0.4
            // Since scores are 0, priority = spanFactor * 0.4
            double expectedPriority = expectedSpanFactor * 0.4;

            assertEquals(expectedPriority, priority, 0.0001);
        }

        @Test
        @DisplayName("Should trigger panic earlier (80% of usable buffer)")
        void testPanicTrigger() {
            // Buffer = 1000 * 0.8 = 800
            // Threshold = 800 * 0.8 = 640
            PanicContext safeCtx = createPanicContext(640.0, 1000.0, 0.2);
            PanicContext panicCtx = createPanicContext(640.1, 1000.0, 0.2);

            assertFalse(strategy.shouldTriggerPanic(safeCtx));
            assertTrue(strategy.shouldTriggerPanic(panicCtx));
        }
    }
}