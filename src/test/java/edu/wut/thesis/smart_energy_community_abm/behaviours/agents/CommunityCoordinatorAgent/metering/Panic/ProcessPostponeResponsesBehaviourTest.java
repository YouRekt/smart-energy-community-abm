package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.metering.Panic;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey;
import jade.core.AID;
import jade.core.behaviours.DataStore;
import jade.lang.acl.ACLMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProcessPostponeResponsesBehaviourTest {

    private ProcessPostponeResponsesBehaviour behaviour;
    private CommunityCoordinatorAgent spyAgent;
    private DataStore mockDataStore;

    // AIDs for testing
    private final AID house1 = new AID("House1@unit-test", AID.ISGUID);
    private final AID house2 = new AID("House2@unit-test", AID.ISGUID);
    private final AID house3 = new AID("House3@unit-test", AID.ISGUID);

    @BeforeEach
    void setUp() {
        // Spy on agent to mock logging and sending
        spyAgent = spy(new CommunityCoordinatorAgent());
        doNothing().when(spyAgent).send(any(ACLMessage.class));
        doNothing().when(spyAgent).log(anyString(), any(), any());

        behaviour = new ProcessPostponeResponsesBehaviour(spyAgent);
        mockDataStore = mock(DataStore.class);
        behaviour.setDataStore(mockDataStore);
    }

    @Test
    @DisplayName("Should stop accepting proposals once shortfall is saturated (Greedy approach)")
    void testSaturationLogic() {
        // Given
        // Shortfall: 15.0
        when(mockDataStore.get(DataStoreKey.Metering.SHORTFALL)).thenReturn(15.0);

        // Responses: House1(10.0), House2(10.0), House3(10.0)
        // Order matters! We use LinkedHashMap to force iteration order: H1 -> H2 -> H3
        Map<AID, Double> responses = new LinkedHashMap<>();
        responses.put(house1, 10.0);
        responses.put(house2, 10.0);
        responses.put(house3, 10.0);

        when(mockDataStore.get(DataStoreKey.Metering.Panic.POSTPONE_RESPONSES)).thenReturn(responses);

        // When
        behaviour.action();

        // Then
        // Logic Trace:
        // 1. House1 (10.0). Total Freed = 10.0. Shortfall (15) > 10. Not Saturated. -> ACCEPT
        // 2. House2 (10.0). Total Freed = 20.0. Shortfall (15) <= 20. Saturated = TRUE. -> ACCEPT
        // 3. House3 (10.0). Saturated is TRUE. -> REJECT

        ArgumentCaptor<ACLMessage> captor = ArgumentCaptor.forClass(ACLMessage.class);
        // Captured twice: 1. Accepted msg, 2. Rejected msg
        verify(spyAgent, times(2)).send(captor.capture());
        List<ACLMessage> sentMessages = captor.getAllValues();

        // Find the specific messages by performative
        ACLMessage acceptMsg = sentMessages.stream()
                .filter(m -> m.getPerformative() == ACLMessage.ACCEPT_PROPOSAL).findFirst().orElseThrow();
        ACLMessage rejectMsg = sentMessages.stream()
                .filter(m -> m.getPerformative() == ACLMessage.REJECT_PROPOSAL).findFirst().orElseThrow();

        // Assert Receivers
        assertTrue(containsReceiver(acceptMsg, house1));
        assertTrue(containsReceiver(acceptMsg, house2));
        assertFalse(containsReceiver(acceptMsg, house3));

        assertFalse(containsReceiver(rejectMsg, house1));
        assertFalse(containsReceiver(rejectMsg, house2));
        assertTrue(containsReceiver(rejectMsg, house3));
    }

    @Test
    @DisplayName("Should accept all if shortfall is NOT saturated")
    void testUndersaturation() {
        // Given
        // Shortfall: 100.0 (Huge)
        when(mockDataStore.get(DataStoreKey.Metering.SHORTFALL)).thenReturn(100.0);

        Map<AID, Double> responses = new LinkedHashMap<>();
        responses.put(house1, 10.0);
        responses.put(house2, 20.0);

        when(mockDataStore.get(DataStoreKey.Metering.Panic.POSTPONE_RESPONSES)).thenReturn(responses);

        // When
        behaviour.action();

        // Then
        ArgumentCaptor<ACLMessage> captor = ArgumentCaptor.forClass(ACLMessage.class);
        verify(spyAgent, times(2)).send(captor.capture());

        ACLMessage acceptMsg = captor.getAllValues().stream()
                .filter(m -> m.getPerformative() == ACLMessage.ACCEPT_PROPOSAL).findFirst().orElseThrow();

        assertTrue(containsReceiver(acceptMsg, house1));
        assertTrue(containsReceiver(acceptMsg, house2));
    }

    @Test
    @DisplayName("Should handle empty responses gracefully")
    void testEmptyResponses() {
        // Given
        when(mockDataStore.get(DataStoreKey.Metering.SHORTFALL)).thenReturn(10.0);
        when(mockDataStore.get(DataStoreKey.Metering.Panic.POSTPONE_RESPONSES)).thenReturn(Collections.emptyMap());

        // When
        behaviour.action();

        // Then
        // Should still send messages, just with no receivers
        verify(spyAgent, times(2)).send(any(ACLMessage.class));
    }

    @Test
    @DisplayName("Should set ReplyBy date on messages")
    void testReplyByDate() {
        when(mockDataStore.get(DataStoreKey.Metering.SHORTFALL)).thenReturn(10.0);
        when(mockDataStore.get(DataStoreKey.Metering.Panic.POSTPONE_RESPONSES)).thenReturn(Collections.emptyMap());

        behaviour.action();

        ArgumentCaptor<ACLMessage> captor = ArgumentCaptor.forClass(ACLMessage.class);
        verify(spyAgent, times(2)).send(captor.capture());

        assertNotNull(captor.getAllValues().get(0).getReplyByDate());
        assertNotNull(captor.getAllValues().get(1).getReplyByDate());

        // Also verify it puts the date in DataStore
        verify(mockDataStore).put(eq(DataStoreKey.Metering.Panic.ACCEPT_PROPOSAL_REPLY_BY), any());
    }

    // Helper to check JADE Iterator receivers
    private boolean containsReceiver(ACLMessage msg, AID target) {
        Iterator it = msg.getAllReceiver();
        while(it.hasNext()) {
            AID r = (AID) it.next();
            if (r.equals(target)) return true;
        }
        return false;
    }
}