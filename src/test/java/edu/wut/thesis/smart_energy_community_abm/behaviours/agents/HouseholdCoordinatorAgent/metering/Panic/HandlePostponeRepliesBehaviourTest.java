package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.metering.Panic;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.AllocationEntry;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey;
import jade.core.AID;
import jade.core.behaviours.DataStore;
import jade.lang.acl.ACLMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HandlePostponeRepliesBehaviourTest {

    private HandlePostponeRepliesBehaviour behaviour;
    private HouseholdCoordinatorAgent spyAgent;
    private DataStore mockDataStore;

    private final AID appliance1 = new AID("Appliance1@unit-test", AID.ISGUID);
    private final AID appliance2 = new AID("Appliance2@unit-test", AID.ISGUID);

    @BeforeEach
    void setUp() {
        // 1. Create a REAL agent to ensure 'timetable' field is initialized
        HouseholdCoordinatorAgent realAgent = new HouseholdCoordinatorAgent();

        // 2. Spy on it to suppress JADE platform interactions (like send)
        spyAgent = spy(realAgent);
        doNothing().when(spyAgent).send(any(ACLMessage.class));

        // 3. Initialize Behaviour with Spy
        behaviour = new HandlePostponeRepliesBehaviour(spyAgent);

        // 4. Mock and attach DataStore
        mockDataStore = mock(DataStore.class);
        behaviour.setDataStore(mockDataStore);

        // 5. Setup common DataStore elements (CFP)
        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
        cfp.setSender(new AID("CommunityCoordinator@unit-test", AID.ISGUID));
        when(mockDataStore.get(DataStoreKey.Metering.PANIC_CFP)).thenReturn(cfp);
    }

    @Test
    @DisplayName("Should reply PROPOSE with summed energy when agreements exist")
    void testProposeWithEnergy() {
        // Given
        long tick = 100L;
        spyAgent.tick = tick;

        // Setup Timetable: Appliance1 has 50.0 energy allocated
        Map<AID, AllocationEntry> tickAllocations = new HashMap<>();
        // AllocationEntry(requestedEnergy, requestTimestamp, allocationStart, duration)
        AllocationEntry entry1 = new AllocationEntry(50.0, tick, 1);
        tickAllocations.put(appliance1, entry1);

        spyAgent.timetable.put(tick, tickAllocations);

        // DataStore: Appliance1 agreed to postpone
        List<AID> agreements = List.of(appliance1);
        when(mockDataStore.get(DataStoreKey.Metering.Panic.POSTPONE_AGREEMENTS)).thenReturn(agreements);

        // When
        behaviour.action();

        // Then
        ArgumentCaptor<ACLMessage> captor = ArgumentCaptor.forClass(ACLMessage.class);
        verify(spyAgent).send(captor.capture());
        ACLMessage reply = captor.getValue();

        assertEquals(ACLMessage.PROPOSE, reply.getPerformative());
        assertEquals("50.0", reply.getContent(), "Content should be the total freed energy");
    }

    @Test
    @DisplayName("Should sum energy correctly for multiple agreeing appliances")
    void testSumMultipleAppliances() {
        // Given
        long tick = 200L;
        spyAgent.tick = tick;

        // Setup Timetable: App1=10.0, App2=20.0
        Map<AID, AllocationEntry> tickAllocations = new HashMap<>();
        tickAllocations.put(appliance1, new AllocationEntry(10.0, tick, 1));
        tickAllocations.put(appliance2, new AllocationEntry(20.0, tick, 1));

        spyAgent.timetable.put(tick, tickAllocations);

        // DataStore: Both agreed
        List<AID> agreements = List.of(appliance1, appliance2);
        when(mockDataStore.get(DataStoreKey.Metering.Panic.POSTPONE_AGREEMENTS)).thenReturn(agreements);

        // When
        behaviour.action();

        // Then
        ArgumentCaptor<ACLMessage> captor = ArgumentCaptor.forClass(ACLMessage.class);
        verify(spyAgent).send(captor.capture());

        assertEquals(ACLMessage.PROPOSE, captor.getValue().getPerformative());
        assertEquals("30.0", captor.getValue().getContent());
    }

    @Test
    @DisplayName("Should reply REFUSE if agreement list is empty")
    void testRefuseEmptyAgreements() {
        // Given
        long tick = 300L;
        spyAgent.tick = tick;
        spyAgent.timetable.put(tick, new HashMap<>()); // Empty allocations

        when(mockDataStore.get(DataStoreKey.Metering.Panic.POSTPONE_AGREEMENTS)).thenReturn(Collections.emptyList());

        // When
        behaviour.action();

        // Then
        ArgumentCaptor<ACLMessage> captor = ArgumentCaptor.forClass(ACLMessage.class);
        verify(spyAgent).send(captor.capture());

        assertEquals(ACLMessage.REFUSE, captor.getValue().getPerformative());
        assertNull(captor.getValue().getContent(), "Refuse usually has null content (or empty)");
    }

    @Test
    @DisplayName("Should reply REFUSE if agreed appliance has no allocation (Safety check)")
    void testRefuseNoAllocationFound() {
        // Given
        long tick = 400L;
        spyAgent.tick = tick;
        spyAgent.timetable.put(tick, new HashMap<>()); // Timetable exists but is empty

        // DataStore: Appliance1 agreed, but isn't in the timetable
        List<AID> agreements = List.of(appliance1);
        when(mockDataStore.get(DataStoreKey.Metering.Panic.POSTPONE_AGREEMENTS)).thenReturn(agreements);

        // When
        behaviour.action();

        // Then
        ArgumentCaptor<ACLMessage> captor = ArgumentCaptor.forClass(ACLMessage.class);
        verify(spyAgent).send(captor.capture());

        assertEquals(ACLMessage.REFUSE, captor.getValue().getPerformative(), "Should refuse if calculated energy is 0");
    }
}