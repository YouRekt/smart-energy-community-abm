package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.metering;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.TransitionKeys;
import jade.core.AID;
import jade.core.behaviours.DataStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CalculateEnergyBalanceBehaviourTest {

    private CalculateEnergyBalanceBehaviour behaviour;
    private CommunityCoordinatorAgent spyAgent;
    private DataStore mockDataStore;

    @BeforeEach
    void setUp() {
        // 1. Spy on a real instance to ensure 'allocations' map is initialized
        spyAgent = spy(new CommunityCoordinatorAgent());

        // Suppress logging to keep test output clean
        doNothing().when(spyAgent).log(anyString(), any(), any());

        // 2. Mock DataStore
        mockDataStore = mock(DataStore.class);

        // 3. Initialize Behaviour
        behaviour = new CalculateEnergyBalanceBehaviour(spyAgent);
        behaviour.setDataStore(mockDataStore);
    }

    @Test
    @DisplayName("Should detect NO PANIC when energy is sufficient (Surplus)")
    void testNoPanicSurplus() {
        // Given
        spyAgent.tick = 10L;

        // Inputs: Charge 50 + Produced 60 = 110 Available
        when(mockDataStore.get(DataStoreKey.Metering.CURRENT_CHARGE)).thenReturn(50.0);
        when(mockDataStore.get(DataStoreKey.Metering.POWER_PRODUCED)).thenReturn(60.0);

        // Mock Agent State: Allocated 100 (Surplus of 10)
        doReturn(100.0).when(spyAgent).getAllocatedAt(10L);

        // When
        behaviour.action();

        // Then
        // 1. Verify Calculations put into DataStore
        verify(mockDataStore).put(DataStoreKey.Metering.AVAILABLE_ENERGY, 110.0);
        verify(mockDataStore).put(DataStoreKey.Metering.SHORTFALL, -10.0); // 100 - 110

        // 2. Verify Result
        assertEquals(TransitionKeys.Metering.NO_PANIC, behaviour.onEnd());
    }

    @Test
    @DisplayName("Should detect NO PANIC if shortfall exists but strategy says ignore")
    void testShortfallIgnored() {
        // Given
        spyAgent.tick = 20L;

        // Inputs: Charge 0 + Produced 50 = 50 Available
        when(mockDataStore.get(DataStoreKey.Metering.CURRENT_CHARGE)).thenReturn(0.0);
        when(mockDataStore.get(DataStoreKey.Metering.POWER_PRODUCED)).thenReturn(50.0);

        // Mock Agent State: Allocated 60 (Shortfall of 10)
        doReturn(60.0).when(spyAgent).getAllocatedAt(20L);

        // Mock Strategy: shouldTriggerPanic -> FALSE
        // (e.g., maybe battery has enough buffer or shortfall is tiny)
        doReturn(false).when(spyAgent).shouldTriggerPanic(anyDouble(), anyDouble());

        // When
        behaviour.action();

        // Then
        verify(mockDataStore).put(DataStoreKey.Metering.SHORTFALL, 10.0);
        assertEquals(TransitionKeys.Metering.NO_PANIC, behaviour.onEnd());
    }

    @Test
    @DisplayName("Should transition to HAS PANIC when strategy confirms trigger")
    void testPanicTriggered() {
        // Given
        long tick = 30L;
        spyAgent.tick = tick;

        // Setup allocations for the tick to test 'householdsAffected' count
        Map<AID, Double> allocations = new HashMap<>();
        allocations.put(new AID("House1@local-test", AID.ISGUID), 10.0);
        allocations.put(new AID("House2@local-test", AID.ISGUID), 20.0);
        spyAgent.allocations.put(tick, allocations);

        // Inputs: Charge 0 + Produced 0 = 0 Available
        when(mockDataStore.get(DataStoreKey.Metering.CURRENT_CHARGE)).thenReturn(0.0);
        when(mockDataStore.get(DataStoreKey.Metering.POWER_PRODUCED)).thenReturn(0.0);

        // Mock Agent State: Allocated 30 (Shortfall 30)
        doReturn(30.0).when(spyAgent).getAllocatedAt(tick);

        // Mock Strategy: shouldTriggerPanic -> TRUE
        doReturn(true).when(spyAgent).shouldTriggerPanic(eq(30.0), eq(0.0));

        // When
        behaviour.action();

        // Then
        verify(mockDataStore).put(DataStoreKey.Metering.SHORTFALL, 30.0);
        assertEquals(TransitionKeys.Metering.HAS_PANIC, behaviour.onEnd());
    }

    @Test
    @DisplayName("Should update running average statistics")
    void testUpdateStatistics() {
        // Given
        spyAgent.tick = 40L;
        when(mockDataStore.get(DataStoreKey.Metering.CURRENT_CHARGE)).thenReturn(0.0);
        when(mockDataStore.get(DataStoreKey.Metering.POWER_PRODUCED)).thenReturn(123.45);
        doReturn(0.0).when(spyAgent).getAllocatedAt(40L);

        // When
        behaviour.action();

        // Then
        // Verify the agent method was called with the production value
        verify(spyAgent).updateRunningAverage(123.45);
    }
}