package edu.wut.thesis.smart_energy_community_abm.domain.config;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.ApplianceTask;
import edu.wut.thesis.smart_energy_community_abm.domain.strategy.interfaces.NegotiationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommunityConfigTest {

    private BatteryConfig validBattery;
    private List<GreenEnergySourceConfig> validGreenSources;
    private List<HouseholdConfig> validHouseholds;

    @BeforeEach
    void setUp() {
        // Setup valid dummy objects to reuse
        validBattery = new BatteryConfig(1000.0, 0.5, true);

        validGreenSources = List.of(
                new GreenEnergySourceConfig(100L, 500.0, 50L, 10.0, 0.1, "WindFarm")
        );

        ApplianceTask task = new ApplianceTask("Task1", 0.5, 100, true, 5, 10.0, 0);
        ApplianceConfig appConfig = new ApplianceConfig("Washer", "House1", List.of(task));
        validHouseholds = List.of(
                new HouseholdConfig(List.of(appConfig), "House1")
        );
    }

    @Test
    @DisplayName("Should create config successfully with valid parameters")
    void testValidCreation() {
        assertDoesNotThrow(() -> new CommunityConfig(
                validBattery,
                validGreenSources,
                validHouseholds,
                "GreenScoreFirst"
        ));
    }

    @Test
    @DisplayName("Should throw if BatteryConfig is null")
    void testBatteryNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new CommunityConfig(null, validGreenSources, validHouseholds, "Balanced")
        );
        assertTrue(ex.getMessage().contains("batteryConfig"), "Exception should mention batteryConfig");
    }

    @ParameterizedTest(name = "EnergySources list: {0}")
    @NullAndEmptySource
    @DisplayName("Should throw if EnergySources list is null or empty")
    void testEnergySourcesInvalid(List<GreenEnergySourceConfig> invalidSources) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new CommunityConfig(validBattery, invalidSources, validHouseholds, "Balanced")
        );
        assertTrue(ex.getMessage().contains("energySourcesConfigs"), "Exception should mention energySourcesConfigs");
    }

    @ParameterizedTest(name = "HouseholdConfigs list: {0}")
    @NullAndEmptySource
    @DisplayName("Should throw if HouseholdConfigs list is null or empty")
    void testHouseholdsInvalid(List<HouseholdConfig> invalidHouseholds) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new CommunityConfig(validBattery, validGreenSources, invalidHouseholds, "Balanced")
        );
        assertTrue(ex.getMessage().contains("householdConfigs"), "Exception should mention householdConfigs");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t"})
    @DisplayName("Should default Strategy Name to 'Balanced' if missing")
    void testStrategyDefaulting(String missingStrategy) {
        // When
        CommunityConfig config = new CommunityConfig(validBattery, validGreenSources, validHouseholds, missingStrategy);

        // Then
        assertEquals("Balanced", config.strategyName(), "Should default to Balanced strategy");
    }

    @Test
    @DisplayName("getAgentParams should return correct agent class and arguments")
    void testGetAgentParams() {
        // Given
        String strategyName = "ReservationFirst";
        CommunityConfig config = new CommunityConfig(validBattery, validGreenSources, validHouseholds, strategyName);

        // When
        AgentParams params = config.getAgentParams();

        // Then
        assertEquals(CommunityCoordinatorAgent.class.getSimpleName(), params.agentName());
        assertEquals(CommunityCoordinatorAgent.class, params.agentClass());

        Object[] args = params.agentArgs();
        assertNotNull(args);
        assertEquals(3, args.length, "Coordinator expects 3 arguments");

        // Arg 0: Household Count
        assertEquals(validHouseholds.size(), args[0]);

        // Arg 1: Energy Source Count
        assertEquals(validGreenSources.size(), args[1]);

        // Arg 2: Strategy Instance
        assertNotNull(args[2]);
        assertTrue(NegotiationStrategy.class.isAssignableFrom(args[2].getClass()),
                "Third argument must be a NegotiationStrategy instance");
        assertEquals(strategyName, ((NegotiationStrategy) args[2]).getName());
    }
}