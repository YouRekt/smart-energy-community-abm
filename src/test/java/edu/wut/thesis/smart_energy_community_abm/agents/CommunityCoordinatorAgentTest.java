package edu.wut.thesis.smart_energy_community_abm.agents;

import jade.core.AID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommunityCoordinatorAgentTest {

    private CommunityCoordinatorAgent agent;

    // We append a fake platform name "@unit-test" to make it a valid GUID format.
    private final AID household1 = new AID("Household1@unit-test", AID.ISGUID);
    private final AID household2 = new AID("Household2@unit-test", AID.ISGUID);

    @BeforeEach
    void setUp() {
        agent = new CommunityCoordinatorAgent();
    }

    // --- Allocation Logic Tests ---

    @Test
    @DisplayName("Should correctly sum allocations for a specific tick")
    void testGetAllocatedAt() {
        // Given
        long tick = 10;
        agent.addAllocation(tick, household1, 100.0);
        agent.addAllocation(tick, household2, 50.0);

        // When
        double total = agent.getAllocatedAt(tick);

        // Then
        assertEquals(150.0, total, 0.0001, "Total allocation should be sum of all households");
    }

    @Test
    @DisplayName("Should return 0.0 if no allocations exist for tick")
    void testGetAllocatedAtEmpty() {
        assertEquals(0.0, agent.getAllocatedAt(999), "Should return 0 for unknown tick");
    }

    @Test
    @DisplayName("Should merge allocations correctly when adding to existing household")
    void testAddAllocationMerge() {
        // Given
        long tick = 20;
        agent.addAllocation(tick, household1, 10.0);

        // When
        agent.addAllocation(tick, household1, 20.0); // Add more to same agent

        // Then
        assertEquals(30.0, agent.getAllocatedAt(tick));
        // Verify internal map state directly
        assertEquals(30.0, agent.allocations.get(tick).get(household1));
    }

    @Test
    @DisplayName("Should remove allocations correctly")
    void testRemoveAllocation() {
        // Given
        long tick = 30;
        agent.addAllocation(tick, household1, 100.0);
        agent.addAllocation(tick, household2, 100.0);

        // When
        agent.removeAllocation(tick, household1);

        // Then
        assertEquals(100.0, agent.getAllocatedAt(tick), "Total should decrease");
        assertNull(agent.allocations.get(tick).get(household1), "Household entry should be removed");
        assertNotNull(agent.allocations.get(tick).get(household2), "Other household should remain");
    }

    @Test
    @DisplayName("Should clean up tick entry when last allocation is removed")
    void testRemoveLastAllocationCleanup() {
        // Given
        long tick = 40;
        agent.addAllocation(tick, household1, 100.0);

        // When
        agent.removeAllocation(tick, household1);

        // Then
        assertFalse(agent.allocations.containsKey(tick), "Tick entry should be removed if empty");
    }

    // --- Statistics Logic Tests ---

    @Test
    @DisplayName("Should calculate iterative running average correctly")
    void testUpdateRunningAverage() {
        // Sequence: 10, 20, 30
        agent.updateRunningAverage(10.0);
        assertEquals(10.0, agent.runningAvgProduction, 0.001);

        agent.updateRunningAverage(20.0);
        assertEquals(15.0, agent.runningAvgProduction, 0.001); // (10+20)/2

        agent.updateRunningAverage(30.0);
        assertEquals(20.0, agent.runningAvgProduction, 0.001); // (15*2 + 30)/3 = 60/3 = 20

        assertEquals(3, agent.productionSampleCount);
    }

    // --- Game Theory/Score Logic Tests ---

    @Test
    @DisplayName("Should increase cooperation score on acceptance (capped at 1.0)")
    void testUpdateCooperationScoreAccept() {
        // 1. First acceptance: 0.5 (default) + 0.05 = 0.55
        agent.updateCooperationScore(household1, true);
        assertEquals(0.55, agent.cooperationScores.get(household1), 0.001);

        // 2. Max out
        for (int i = 0; i < 20; i++) {
            agent.updateCooperationScore(household1, true);
        }
        assertEquals(1.0, agent.cooperationScores.get(household1), 0.001, "Score should handle upper bound");
    }

    @Test
    @DisplayName("Should decrease cooperation score on rejection (capped at 0.0)")
    void testUpdateCooperationScoreReject() {
        // 1. First rejection: 0.5 (default) - 0.03 = 0.47
        agent.updateCooperationScore(household1, false);
        assertEquals(0.47, agent.cooperationScores.get(household1), 0.001);

        // 2. Bottom out
        for (int i = 0; i < 20; i++) {
            agent.updateCooperationScore(household1, false);
        }
        assertEquals(0.0, agent.cooperationScores.get(household1), 0.001, "Score should handle lower bound");
    }
}