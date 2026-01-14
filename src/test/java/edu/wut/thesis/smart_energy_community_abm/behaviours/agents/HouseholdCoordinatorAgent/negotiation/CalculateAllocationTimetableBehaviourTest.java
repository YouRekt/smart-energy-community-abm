package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.negotiation;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.EnergyRequest;
import jade.core.AID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CalculateAllocationTimetableBehaviourTest {

    private CalculateAllocationTimetableBehaviour behaviour;
    private Method solveSubsetSumMethod;

    @BeforeEach
    void setUp() throws Exception {
        // Mock the agent to satisfy the constructor (though we won't use it for the algo test)
        HouseholdCoordinatorAgent mockAgent = mock(HouseholdCoordinatorAgent.class);
        behaviour = new CalculateAllocationTimetableBehaviour(mockAgent);

        // Access the private method via Reflection
        solveSubsetSumMethod = CalculateAllocationTimetableBehaviour.class.getDeclaredMethod("solveSubsetSum", List.class, double.class);
        solveSubsetSumMethod.setAccessible(true);
    }

    /**
     * Helper to invoke the private method.
     */
    @SuppressWarnings("unchecked")
    private Set<EnergyRequest> invokeSolveSubsetSum(List<EnergyRequest> items, double limit) {
        try {
            return (Set<EnergyRequest>) solveSubsetSumMethod.invoke(behaviour, items, limit);
        } catch (Exception e) {
            throw new RuntimeException("Reflection failed", e);
        }
    }

    private EnergyRequest createRequest(double energy) {
        // Tick and duration don't matter for the subset sum logic, only energyPerTick
        return new EnergyRequest(new AID("Appliance1@local-test", AID.ISGUID),0, 1, energy);
    }

    @Test
    @DisplayName("Should return empty set when input list is empty")
    void testEmptyInput() {
        Set<EnergyRequest> result = invokeSolveSubsetSum(Collections.emptyList(), 100.0);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty set when limit is zero")
    void testZeroLimit() {
        List<EnergyRequest> items = List.of(createRequest(10.0));
        Set<EnergyRequest> result = invokeSolveSubsetSum(items, 0.0);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should select all items if they fit exactly")
    void testExactFit() {
        EnergyRequest r1 = createRequest(2.0);
        EnergyRequest r2 = createRequest(3.0);
        EnergyRequest r3 = createRequest(5.0);
        List<EnergyRequest> items = List.of(r1, r2, r3);

        Set<EnergyRequest> result = invokeSolveSubsetSum(items, 10.0);

        assertEquals(3, result.size());
        assertTrue(result.containsAll(items));
    }

    @Test
    @DisplayName("Should select the subset closest to the limit (Standard Knapsack)")
    void testBestFit() {
        // Limit: 10
        // Items: 8, 5, 4
        // Combinations:
        // [8] -> 8
        // [5, 4] -> 9 (Winner)
        EnergyRequest r8 = createRequest(8.0);
        EnergyRequest r5 = createRequest(5.0);
        EnergyRequest r4 = createRequest(4.0);
        List<EnergyRequest> items = List.of(r8, r5, r4);

        Set<EnergyRequest> result = invokeSolveSubsetSum(items, 10.0);

        assertEquals(2, result.size(), "Should verify that 2 items (5+4) provide a sum of 9, which is > 8");
        assertTrue(result.contains(r5));
        assertTrue(result.contains(r4));
        assertFalse(result.contains(r8));
    }

    @Test
    @DisplayName("Should exclude items that are strictly larger than the limit")
    void testItemTooLarge() {
        EnergyRequest rSmall = createRequest(5.0);
        EnergyRequest rLarge = createRequest(15.0);
        List<EnergyRequest> items = List.of(rSmall, rLarge);

        Set<EnergyRequest> result = invokeSolveSubsetSum(items, 10.0);

        assertEquals(1, result.size());
        assertTrue(result.contains(rSmall));
        assertFalse(result.contains(rLarge));
    }

    @ParameterizedTest
    @MethodSource("floatingPointScenarios")
    @DisplayName("Should handle floating point precision correctly")
    void testFloatingPointPrecision(List<Double> itemValues, double limit, double expectedSum) {
        List<EnergyRequest> items = itemValues.stream()
                .map(this::createRequest)
                .toList();

        Set<EnergyRequest> result = invokeSolveSubsetSum(items, limit);

        double actualSum = result.stream().mapToDouble(EnergyRequest::energyPerTick).sum();
        assertEquals(expectedSum, actualSum, 0.001, "Sum of selected items should match expected best fit");
    }

    static Stream<Arguments> floatingPointScenarios() {
        return Stream.of(
                // 0.1 + 0.2 = 0.3 (classic FP issue), Limit 0.3
                Arguments.of(List.of(0.1, 0.2, 0.5), 0.3, 0.3),

                // Items: 0.333, 0.333, 0.334. Limit: 1.0. Sum: 1.0
                Arguments.of(List.of(0.333, 0.333, 0.334), 1.0, 1.0),

                // Items: 1.001. Limit: 1.0. Should reject.
                Arguments.of(List.of(1.001), 1.0, 0.0)
        );
    }

    @Test
    @DisplayName("Should verify DP correctness with complex mix")
    void testComplexMix() {
        // A set of items where greedy approach (taking biggest first) fails
        // Limit: 6
        // Items: 4, 3, 3
        // Greedy takes 4, remaining space 2 -> total 4.
        // Optimal takes 3 + 3 -> total 6.
        EnergyRequest r4 = createRequest(4.0);
        EnergyRequest r3a = createRequest(3.0);
        EnergyRequest r3b = createRequest(3.0);
        List<EnergyRequest> items = List.of(r4, r3a, r3b);

        Set<EnergyRequest> result = invokeSolveSubsetSum(items, 6.0);

        assertEquals(2, result.size());
        assertFalse(result.contains(r4));
        assertTrue(result.contains(r3a));
        assertTrue(result.contains(r3b));
    }
}