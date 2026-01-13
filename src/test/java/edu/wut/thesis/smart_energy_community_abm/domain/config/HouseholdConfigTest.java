package edu.wut.thesis.smart_energy_community_abm.domain.config;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.ApplianceTask;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HouseholdConfigTest {

    // Helper to create a valid ApplianceConfig
    private ApplianceConfig createDummyApplianceConfig() {
        ApplianceTask task = new ApplianceTask("Task1", 0.5, 100, true, 5, 10.0);
        return new ApplianceConfig("Dishwasher", "Household1", List.of(task));
    }

    @Test
    @DisplayName("Should create config successfully with valid parameters")
    void testValidCreation() {
        List<ApplianceConfig> appliances = List.of(createDummyApplianceConfig());

        assertDoesNotThrow(() -> new HouseholdConfig(
                appliances,
                "Household1"
        ));
    }

    @ParameterizedTest(name = "HouseholdName '{0}' should be invalid")
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t\n"})
    @DisplayName("Should throw exception if householdName is null or blank")
    void testInvalidHouseholdName(String invalidName) {
        List<ApplianceConfig> appliances = List.of(createDummyApplianceConfig());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new HouseholdConfig(appliances, invalidName)
        );

        assertTrue(ex.getMessage().contains("householdName"), "Exception should mention householdName");
    }

    @Test
    @DisplayName("Should throw exception if applianceConfigs list is null")
    void testApplianceConfigsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new HouseholdConfig(null, "Household1")
        );
        assertTrue(ex.getMessage().contains("applianceConfigs"), "Exception should mention applianceConfigs");
    }

    @Test
    @DisplayName("Should throw exception if applianceConfigs list is empty")
    void testApplianceConfigsEmpty() {
        List<ApplianceConfig> emptyList = Collections.emptyList();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new HouseholdConfig(emptyList, "Household1")
        );
        assertTrue(ex.getMessage().contains("applianceConfigs"), "Exception should mention applianceConfigs");
    }

    @Test
    @DisplayName("getAgentParams should return correct agent class and argument (Size count)")
    void testGetAgentParams() {
        // Given
        String householdName = "HouseholdZ";
        // Create list with 2 appliances
        List<ApplianceConfig> appliances = List.of(
                createDummyApplianceConfig(),
                createDummyApplianceConfig()
        );

        HouseholdConfig config = new HouseholdConfig(appliances, householdName);

        // When
        AgentParams params = config.getAgentParams();

        // Then
        assertEquals(householdName, params.agentName());
        assertEquals(HouseholdCoordinatorAgent.class, params.agentClass());

        Object[] args = params.agentArgs();
        assertNotNull(args);
        assertEquals(1, args.length, "HouseholdCoordinator expects exactly 1 argument (applianceCount)");

        // IMPORTANT: The HouseholdCoordinatorAgent expects an Integer (count), not the List.
        assertEquals(2, args[0], "Argument should be the size of the appliance list");
    }
}