package edu.wut.thesis.smart_energy_community_abm.domain.config;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.ApplianceTask;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApplianceConfigTest {

    // Helper to create a dummy task for testing
    private ApplianceTask createDummyTask() {
        return new ApplianceTask("TestTask", 0.5, 100, true, 5, 10.0, 0);
    }

    @Test
    @DisplayName("Should create config successfully with valid parameters")
    void testValidCreation() {
        List<ApplianceTask> tasks = List.of(createDummyTask());

        assertDoesNotThrow(() -> new ApplianceConfig(
                "MyDishwasher",
                "Household1",
                tasks
        ));
    }

    @ParameterizedTest(name = "HouseholdName '{0}' should be invalid")
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t\n"})
    @DisplayName("Should throw exception if householdName is null or blank")
    void testInvalidHouseholdName(String invalidName) {
        List<ApplianceTask> tasks = List.of(createDummyTask());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new ApplianceConfig("MyDishwasher", invalidName, tasks)
        );

        assertTrue(ex.getMessage().contains("householdName"), "Exception should mention householdName");
    }

    @Test
    @DisplayName("Should throw exception if tasks list is null")
    void testTasksNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new ApplianceConfig("MyDishwasher", "Household1", null)
        );
        assertTrue(ex.getMessage().contains("tasks"), "Exception should mention tasks");
    }

    @Test
    @DisplayName("Should throw exception if tasks list is empty")
    void testTasksEmpty() {
        List<ApplianceTask> emptyTasks = Collections.emptyList();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new ApplianceConfig("MyDishwasher", "Household1", emptyTasks)
        );
        assertTrue(ex.getMessage().contains("tasks"), "Exception should mention tasks");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    @DisplayName("Should use default ApplianceAgent name if applianceName is missing")
    void testDefaultApplianceName(String missingName) {
        // Given
        List<ApplianceTask> tasks = List.of(createDummyTask());

        // When
        ApplianceConfig config = new ApplianceConfig(missingName, "Household1", tasks);

        // Then
        assertEquals(ApplianceAgent.class.getSimpleName(), config.applianceName(),
                "Should default to class simple name");
    }

    @Test
    @DisplayName("getAgentParams should return correct agent class and arguments")
    void testGetAgentParams() {
        // Given
        String applianceName = "TestFridge";
        String householdName = "HouseholdX";
        List<ApplianceTask> tasks = List.of(createDummyTask());
        ApplianceConfig config = new ApplianceConfig(applianceName, householdName, tasks);

        // When
        AgentParams params = config.getAgentParams();

        // Then
        assertEquals(applianceName, params.agentName());
        assertEquals(ApplianceAgent.class, params.agentClass());

        Object[] args = params.agentArgs();
        assertNotNull(args);
        assertEquals(2, args.length);
        assertEquals(householdName, args[0]);
        assertEquals(tasks, args[1]);
    }
}