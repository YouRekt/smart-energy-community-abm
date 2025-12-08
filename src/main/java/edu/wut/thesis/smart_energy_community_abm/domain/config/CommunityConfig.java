package edu.wut.thesis.smart_energy_community_abm.domain.config;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.config.interfaces.AgentConfig;
import edu.wut.thesis.smart_energy_community_abm.domain.strategy.StrategyFactory;

import java.util.List;

public record CommunityConfig(
        BatteryConfig batteryConfig,
        List<GreenEnergySourceConfig> energySourcesConfigs,
        List<HouseholdConfig> householdConfigs,
        String strategyName
) implements AgentConfig {

    public CommunityConfig {
        if (energySourcesConfigs == null || energySourcesConfigs.isEmpty())
            throw new IllegalArgumentException("energySourcesConfigs cannot be null or empty");

        if (householdConfigs == null || householdConfigs.isEmpty())
            throw new IllegalArgumentException("householdConfigs cannot be null or empty");

        if (batteryConfig == null)
            throw new IllegalArgumentException("batteryConfig cannot be null");

        if (strategyName == null || strategyName.isBlank()) {
            strategyName = "Balanced";
        }

        energySourcesConfigs = List.copyOf(energySourcesConfigs);
        householdConfigs = List.copyOf(householdConfigs);
    }

    @Override
    public AgentParams getAgentParams() {
        return new AgentParams(CommunityCoordinatorAgent.class.getSimpleName(),
                CommunityCoordinatorAgent.class,
                new Object[]{
                        householdConfigs.size(),
                        energySourcesConfigs.size(),
                        StrategyFactory.create(strategyName),
                });
    }
}