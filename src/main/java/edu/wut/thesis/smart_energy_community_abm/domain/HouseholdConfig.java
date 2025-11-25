package edu.wut.thesis.smart_energy_community_abm.domain;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.interfaces.AgentConfig;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public final class HouseholdConfig implements AgentConfig {
    @Getter
    private final List<ApplianceConfig> applianceConfigs = new ArrayList<>();
    private final String householdName;

    public HouseholdConfig(String householdName, List<ApplianceConfig> applianceConfigs) {
        if (householdName == null || householdName.isBlank()) {
            throw new IllegalArgumentException("householdName is null or empty");
        }

        if (applianceConfigs == null || applianceConfigs.isEmpty()) {
            throw new IllegalArgumentException("applianceConfigs is null or empty");
        }

        this.householdName = householdName;
        this.getApplianceConfigs().addAll(applianceConfigs);
    }

    @Override
    public AgentParams getAgentParams() {
        return new AgentParams(householdName, HouseholdCoordinatorAgent.class, new Object[]{getApplianceConfigs().size()});
    }

}
