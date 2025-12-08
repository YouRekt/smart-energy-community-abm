package edu.wut.thesis.smart_energy_community_abm.domain.config;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.config.interfaces.AgentConfig;

import java.util.List;

public record HouseholdConfig(
        List<ApplianceConfig> applianceConfigs,
        String householdName
) implements AgentConfig {
    public HouseholdConfig {
        if (householdName == null || householdName.isBlank()) {
            throw new IllegalArgumentException("householdName is null or empty");
        }

        if (applianceConfigs == null || applianceConfigs.isEmpty()) {
            throw new IllegalArgumentException("applianceConfigs is null or empty");
        }

        applianceConfigs = List.copyOf(applianceConfigs);
    }

    @Override
    public AgentParams getAgentParams() {
        return new AgentParams(
                householdName,
                HouseholdCoordinatorAgent.class,
                new Object[]{
                        applianceConfigs.size()
                });
    }
}
