package edu.wut.thesis.smart_energy_community_abm.agents;

import edu.wut.thesis.smart_energy_community_abm.domain.AllocationEntry;
import jade.core.AID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HouseholdCoordinatorAgentTest {

    private HouseholdCoordinatorAgent agent;
    private final AID applianceAID = new AID("Appliance1@unit-test", AID.ISGUID);

    @BeforeEach
    void setUp() {
        agent = new HouseholdCoordinatorAgent();
        // Initialize the outer map of the timetable
        agent.timetable.put(10L, new HashMap<>());
        agent.timetable.put(11L, new HashMap<>());
    }

    @Test
    @DisplayName("clearCurrentAllocation: Should remove allocation and return cleared energy")
    void testClearCurrentAllocation() {
        // Given an allocation at tick 10
        long tick = 10L;
        agent.tick = tick;

        // Entry details: Energy 50.0, RequestTime 0, Start 10, Duration 1
        // Note: The code uses `t <= allocationStart()`.
        // If start is 10 and tick is 10, loop runs once (10 <= 10).
        AllocationEntry entry = new AllocationEntry(50.0, 0L, tick, 1);

        agent.timetable.get(tick).put(applianceAID, entry);

        // When
        Map<Long, Double> result = agent.clearCurrentAllocation(applianceAID);

        // Then
        // 1. Verify return value
        assertTrue(result.containsKey(tick));
        assertEquals(50.0, result.get(tick));

        // 2. Verify side effect (removal)
        assertFalse(agent.timetable.get(tick).containsKey(applianceAID), "Appliance should be removed from timetable");
    }

    @Test
    @DisplayName("Logic Check: Ensure loop bounds behave as expected")
    void testClearAllocationLoopBehavior() {
        // This test documents the behavior of the loop: t <= allocationStart()
        long tick = 10L;
        agent.tick = tick;

        // If allocationStart is 10, loop runs for t=10.
        AllocationEntry entry = new AllocationEntry(50.0, 0L, 10L, 1);
        agent.timetable.get(tick).put(applianceAID, entry);

        agent.clearCurrentAllocation(applianceAID);

        assertNull(agent.timetable.get(tick).get(applianceAID));
    }
}