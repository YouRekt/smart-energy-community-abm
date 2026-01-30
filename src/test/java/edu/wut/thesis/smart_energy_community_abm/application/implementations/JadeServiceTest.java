package edu.wut.thesis.smart_energy_community_abm.application.implementations;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import edu.wut.thesis.smart_energy_community_abm.application.JadeService;
import edu.wut.thesis.smart_energy_community_abm.domain.config.CommunityConfig;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class JadeServiceTest {

    private JadeService jadeService;
    private ContainerController mockContainer;
    private AgentController mockAgentController;

    @BeforeEach
    void setUp() throws Exception {
        jadeService = new JadeService();
        mockContainer = mock(ContainerController.class);
        mockAgentController = mock(AgentController.class);

        // Mock container returning a dummy agent controller
        when(mockContainer.createNewAgent(anyString(), anyString(), any(Object[].class)))
                .thenReturn(mockAgentController);

        // Inject the mock container into the service using Reflection
        // (This bypasses the 'startSimulation' method which spins up the real JADE runtime)
        Field containerField = JadeService.class.getDeclaredField("mainContainer");
        containerField.setAccessible(true);
        containerField.set(jadeService, mockContainer);
    }

    @Test
    @DisplayName("Should generate unique names for sequential agents of same type")
    void testAgentNamingLogic() throws StaleProxyException {
        // Given
        String baseName = "Household";
        Class<?> agentClass = ApplianceAgent.class;
        Object[] args = new Object[]{};

        // When: Run agent 3 times with the same nickname
        jadeService.runAgent(agentClass, args, baseName);
        jadeService.runAgent(agentClass, args, baseName);
        jadeService.runAgent(agentClass, args, baseName);

        // Then: Capture the names passed to the container
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockContainer, times(3)).createNewAgent(nameCaptor.capture(), eq(agentClass.getName()), eq(args));

        var usedNames = nameCaptor.getAllValues();

        // Expected Pattern: "Name", "Name[2]", "Name[3]"
        assertEquals(3, usedNames.size());
        assertEquals("Household", usedNames.get(0));
        assertEquals("Household[2]", usedNames.get(1));
        assertEquals("Household[3]", usedNames.get(2));
    }

    @Test
    @DisplayName("Should use class simple name if nickname is null")
    void testNullNicknameDefaults() throws StaleProxyException {
        // Given
        Class<?> agentClass = ApplianceAgent.class; // SimpleName is "ApplianceAgent"

        // When
        jadeService.runAgent(agentClass, new Object[]{}, null);

        // Then
        verify(mockContainer).createNewAgent(eq("ApplianceAgent"), eq(agentClass.getName()), any());
    }

    @Test
    @DisplayName("Should pass arguments correctly to JADE container")
    void testArgumentPassing() throws StaleProxyException {
        // Given
        Object[] args = new Object[]{"Config1", 123};

        // When
        jadeService.runAgent(ApplianceAgent.class, args, "TestAgent");

        // Then
        verify(mockContainer).createNewAgent(anyString(), anyString(), same(args));
    }

    @Test
    @DisplayName("configureSimulation should validate non-null config")
    void testConfigureSimulation() {
        assertThrows(IllegalArgumentException.class, () -> jadeService.configureSimulation(null));

        CommunityConfig mockConfig = mock(CommunityConfig.class);
        assertDoesNotThrow(() -> jadeService.configureSimulation(mockConfig));
    }

    @Test
    @DisplayName("stopSimulation should kill container if running")
    void testStopSimulation() throws StaleProxyException {
        // When
        jadeService.stopSimulation();

        // Then
        verify(mockContainer).kill();

        // Verify internal state is cleared (by trying to stop again, should warn/do nothing, or throw if field not null)
        // Since we can't easily check the private field, we verify behavior.
        // If we call stop again, kill() should NOT be called on the *same* mock if reference was nullified.
        jadeService.stopSimulation();
        verify(mockContainer, times(1)).kill();
    }
}