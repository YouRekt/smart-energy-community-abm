package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.negotiation;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.TransitionKeys;
import jade.core.AID;
import jade.core.behaviours.DataStore;
import jade.lang.acl.ACLMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class RespondToHouseholdsRequestBehaviourTest {

    private RespondToHouseholdsRequestBehaviour behaviour;
    private CommunityCoordinatorAgent mockAgent;
    private DataStore mockDataStore;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockAgent = mock(CommunityCoordinatorAgent.class);
        mockDataStore = mock(DataStore.class);

        behaviour = new RespondToHouseholdsRequestBehaviour(mockAgent);
        behaviour.setDataStore(mockDataStore);
        behaviour.onStart(); // Reset overloaded flag
    }

    private ACLMessage createRequestMessage(Map<Long, Double> requestMap) throws Exception {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setSender(new AID("Household1@local-test", AID.ISGUID));
        msg.setContent(objectMapper.writeValueAsString(requestMap));
        return msg;
    }

    @Test
    @DisplayName("Should CONFIRM and add allocation when request fits within capacity")
    void testRequestFits() throws Exception {
        // Given
        // Request: 10.0 energy at tick 100
        Map<Long, Double> requestMap = Map.of(100L, 10.0);
        ACLMessage requestMsg = createRequestMessage(requestMap);

        when(mockDataStore.get(DataStoreKey.Negotiation.HOUSEHOLD_RESPONSE)).thenReturn(requestMsg);

        // Grid State: 50.0 allocated, Max is 100.0
        // Result: 50 + 10 = 60 < 100 (OK)
        when(mockAgent.getAllocatedAt(100L)).thenReturn(50.0);
        when(mockAgent.getPredictedMaxAmount(100L)).thenReturn(100.0);

        // When
        behaviour.action();

        // Then
        // 1. Verify Transition State
        assertEquals(TransitionKeys.Negotiation.NOT_OVERLOADED, behaviour.onEnd());

        // 2. Verify Allocation was added to Agent
        verify(mockAgent).addAllocation(eq(100L), any(AID.class), eq(10.0));

        // 3. Verify Reply is CONFIRM
        ArgumentCaptor<ACLMessage> captor = ArgumentCaptor.forClass(ACLMessage.class);
        verify(mockAgent).send(captor.capture());
        ACLMessage sentMsg = captor.getValue();
        assertEquals(ACLMessage.CONFIRM, sentMsg.getPerformative());
    }

    @Test
    @DisplayName("Should INFORM (Refuse) with details when request causes overload")
    void testRequestOverloads() throws Exception {
        // Given
        // Request: 20.0 energy at tick 100
        Map<Long, Double> requestMap = Map.of(100L, 20.0);
        ACLMessage requestMsg = createRequestMessage(requestMap);

        when(mockDataStore.get(DataStoreKey.Negotiation.HOUSEHOLD_RESPONSE)).thenReturn(requestMsg);

        // Grid State: 90.0 allocated, Max is 100.0
        // Result: 90 + 20 = 110 > 100 (Overload by 10.0)
        when(mockAgent.getAllocatedAt(100L)).thenReturn(90.0);
        when(mockAgent.getPredictedMaxAmount(100L)).thenReturn(100.0);

        // When
        behaviour.action();

        // Then
        // 1. Verify Transition State
        assertEquals(TransitionKeys.Negotiation.OVERLOADED, behaviour.onEnd());

        // 2. Verify Allocation was NOT added
        verify(mockAgent, never()).addAllocation(anyLong(), any(AID.class), anyDouble());

        // 3. Verify Reply is INFORM (containing overloaded ticks)
        ArgumentCaptor<ACLMessage> captor = ArgumentCaptor.forClass(ACLMessage.class);
        verify(mockAgent).send(captor.capture());
        ACLMessage sentMsg = captor.getValue();

        assertEquals(ACLMessage.INFORM, sentMsg.getPerformative());

        // 4. Verify Content describes the overload
        Map<String, Double> responseContent = objectMapper.readValue(sentMsg.getContent(), Map.class);
        assertTrue(responseContent.containsKey("100"));
        assertEquals(10.0, responseContent.get("100"), 0.001, "Should report exactly the overloaded amount");
    }

    @Test
    @DisplayName("Should handle mixed ticks (some valid, some overloaded)")
    void testMixedTicks() throws Exception {
        // Given
        // Tick 100: Fits (Req 10, alloc 50, max 100)
        // Tick 200: Overloads (Req 10, alloc 95, max 100) -> Overload 5
        Map<Long, Double> requestMap = new HashMap<>();
        requestMap.put(100L, 10.0);
        requestMap.put(200L, 10.0);

        ACLMessage requestMsg = createRequestMessage(requestMap);
        when(mockDataStore.get(DataStoreKey.Negotiation.HOUSEHOLD_RESPONSE)).thenReturn(requestMsg);

        when(mockAgent.getAllocatedAt(100L)).thenReturn(50.0);
        when(mockAgent.getPredictedMaxAmount(100L)).thenReturn(100.0);

        when(mockAgent.getAllocatedAt(200L)).thenReturn(95.0);
        when(mockAgent.getPredictedMaxAmount(200L)).thenReturn(100.0);

        // When
        behaviour.action();

        // Then
        assertEquals(TransitionKeys.Negotiation.OVERLOADED, behaviour.onEnd());

        // Verify content only contains the overloaded tick
        ArgumentCaptor<ACLMessage> captor = ArgumentCaptor.forClass(ACLMessage.class);
        verify(mockAgent).send(captor.capture());
        Map<String, Double> responseContent = objectMapper.readValue(captor.getValue().getContent(), Map.class);

        assertTrue(responseContent.containsKey("200"));
        assertFalse(responseContent.containsKey("100"), "Should not report valid ticks as overloaded");
        assertEquals(5.0, responseContent.get("200"));
    }
}