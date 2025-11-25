package edu.wut.thesis.smart_energy_community_abm.domain;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.interfaces.AgentConfig;

import java.util.List;

public record CommunityConfig(
        BatteryConfig batteryConfig,
        List<GreenEnergySourceConfig> energySourcesConfigs,
        List<HouseholdConfig> householdConfigs
) implements AgentConfig {

    public CommunityConfig {
        if (energySourcesConfigs == null || energySourcesConfigs.isEmpty())
            throw new IllegalArgumentException("energySourcesConfigs cannot be null or empty");

        if (householdConfigs == null || householdConfigs.isEmpty())
            throw new IllegalArgumentException("householdConfigs cannot be null or empty");

        if (batteryConfig == null)
            throw new IllegalArgumentException("batteryConfig cannot be null");

        energySourcesConfigs = List.copyOf(energySourcesConfigs);
        householdConfigs = List.copyOf(householdConfigs);
    }

    @Override
    public AgentParams getAgentParams() {
        return new AgentParams(CommunityCoordinatorAgent.class.getSimpleName(),
                CommunityCoordinatorAgent.class,
                new Object[]{
                        householdConfigs.size(),
                        energySourcesConfigs.size()
                });
    }
}