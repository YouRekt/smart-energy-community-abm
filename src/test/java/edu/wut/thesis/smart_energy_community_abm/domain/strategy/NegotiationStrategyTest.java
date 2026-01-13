package edu.wut.thesis.smart_energy_community_abm.domain.strategy;

import edu.wut.thesis.smart_energy_community_abm.domain.AllocationEntry;
import edu.wut.thesis.smart_energy_community_abm.domain.strategy.implementations.BalancedStrategy;
import edu.wut.thesis.smart_energy_community_abm.domain.strategy.implementations.EnergyVolumeStrategy;
import edu.wut.thesis.smart_energy_community_abm.domain.strategy.implementations.GreenScoreFirstStrategy;
import edu.wut.thesis.smart_energy_community_abm.domain.strategy.implementations.ReservationFirstStrategy;
import edu.wut.thesis.smart_energy_community_abm.domain.strategy.interfaces.NegotiationStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class NegotiationStrategyTest {
    // Helper to create a standard context
    private PriorityContext createPriorityContext(long currentTick, long requestTimestamp, double greenScore, double cooperationScore, double totalEnergy) {
        AllocationEntry entry = new AllocationEntry(totalEnergy, requestTimestamp, currentTick, 1);
        return new PriorityContext(entry, currentTick, greenScore, cooperationScore, totalEnergy);
    }

    private PanicContext createPanicContext(double shortfall, double batteryCharge, double minThreshold) {
        return new PanicContext(shortfall, batteryCharge, minThreshold, 1);
    }

    @Nested
    @DisplayName("Balanced Strategy Tests")
    class BalancedStrategyTests {
        private final NegotiationStrategy strategy = new BalancedStrategy();

        @Test
        @DisplayName("Should compute priority using balanced weights (0.3 Green, 0.5 Reservation, 0.2 Cooperation)")
        void testComputePriority() {
            // Given
            long currentTick = 100;
            long requestTimestamp = 100; // age = 0, reservationBonus = 1 - e^0 = 0
            double greenScore = 0.0;     // weight = 1.0 - 0.0 = 1.0
            double cooperationScore = 1.0;

            // When
            PriorityContext ctx = createPriorityContext(currentTick, requestTimestamp, greenScore, cooperationScore, 10.0);
            double priority = strategy.computePriority(ctx);

            // Then
            // Calculation:
            // Green Part: (1.0 - 0.0) * 0.3 = 0.3
            // Reservation Part: (1.0 - exp(0)) * 0.5 = 0.0
            // Cooperation Part: 1.0 * 0.2 = 0.2
            // Total: 0.5
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
        @DisplayName("Should heavily weigh green score (0.6)")
        void testComputePriority() {
            // Given
            long currentTick = 100;
            long requestTimestamp = 100; // age 0 -> bonus 0
            double greenScore = 0.0;     // low green score -> high weight (1 - 0)
            double cooperationScore = 0.0;

            PriorityContext ctx = createPriorityContext(currentTick, requestTimestamp, greenScore, cooperationScore, 10.0);
            double priority = strategy.computePriority(ctx);

            // Green Part: 1.0 * 0.6 = 0.6
            // Others 0
            assertEquals(0.6, priority, 0.0001);
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
        @DisplayName("Should prioritize early reservations (Decay=30.0, Weight=0.7)")
        void testComputePriority() {
            // Given
            long currentTick = 130;
            long requestTimestamp = 100; // age = 30
            // Bonus = 1 - exp(-30/30) = 1 - e^-1 ~= 1 - 0.3679 = 0.6321
            double greenScore = 1.0;     // weight 0
            double cooperationScore = 0.0;

            PriorityContext ctx = createPriorityContext(currentTick, requestTimestamp, greenScore, cooperationScore, 10.0);
            double priority = strategy.computePriority(ctx);

            double expectedReservationBonus = 1.0 - Math.exp(-1.0);
            double expectedValue = expectedReservationBonus * 0.7;

            assertEquals(expectedValue, priority, 0.0001);
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
                "0.0,  1.0",   // Small energy -> high factor
                "100.0, 0.59"  // Large energy (approx) -> lower factor. 1 / (1 + ln(1 + 100/100)) = 1 / (1 + ln 2) ~= 1/1.693
        })
        @DisplayName("Should penalize high energy requests")
        void testEnergyFactor(double energy, double expectedFactorRange) {
            // Given
            long currentTick = 100;
            long requestTimestamp = 100;
            double greenScore = 1.0; // 0 weight
            double cooperationScore = 0.0;

            PriorityContext ctx = createPriorityContext(currentTick, requestTimestamp, greenScore, cooperationScore, energy);
            double priority = strategy.computePriority(ctx);

            // Energy Weight is 0.2
            // Formula: 1.0 / (1.0 + Math.log1p(energy / 100.0))
            double expectedFactor = 1.0 / (1.0 + Math.log1p(energy / 100.0));
            double expectedPriority = expectedFactor * 0.2;

            assertEquals(expectedPriority, priority, 0.01);
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