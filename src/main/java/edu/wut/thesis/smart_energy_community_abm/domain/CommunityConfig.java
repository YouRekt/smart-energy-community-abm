package edu.wut.thesis.smart_energy_community_abm.domain;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.interfaces.AgentConfig;

import java.util.ArrayList;
import java.util.List;

public class CommunityConfig implements AgentConfig {
    public final List<GreenEnergySourceConfig> energySourcesConfigs = new ArrayList<>();
    public final List<HouseholdConfig> householdConfigs = new ArrayList<>();
    public final BatteryConfig batteryConfig;

    public CommunityConfig(List<GreenEnergySourceConfig> energySourcesConfigs, BatteryConfig batteryConfig, List<HouseholdConfig> householdConfigs) {
        if (energySourcesConfigs == null || energySourcesConfigs.isEmpty())
            throw new IllegalArgumentException("energySourcesConfigs cannot be null or empty");

        if (householdConfigs == null || householdConfigs.isEmpty())
            throw new IllegalArgumentException("householdConfigs cannot be null or empty");

        if (batteryConfig == null)
            throw new IllegalArgumentException("batteryConfig cannot be null");

        this.batteryConfig = batteryConfig;
        this.energySourcesConfigs.addAll(energySourcesConfigs);
        this.householdConfigs.addAll(householdConfigs);
    }

    @Override
    public AgentParams getAgentParams() {
        return new AgentParams(CommunityCoordinatorAgent.class.getSimpleName(), CommunityCoordinatorAgent.class, new Object[]{householdConfigs.size(), energySourcesConfigs.size()});
    }
}
