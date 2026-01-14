package edu.wut.thesis.smart_energy_community_abm.domain.config;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.ApplianceTask;
import edu.wut.thesis.smart_energy_community_abm.domain.config.interfaces.AgentConfig;

import java.util.List;

public record ApplianceConfig(
        String applianceName,
        String householdName,
        List<ApplianceTask> tasks
) implements AgentConfig {

    public ApplianceConfig {
        if (householdName == null || householdName.isBlank()) {
            throw new IllegalArgumentException("householdName is null or empty");
        }

        if (tasks == null || tasks.isEmpty()) {
            throw new IllegalArgumentException("tasks is null or empty");
        }

        if (applianceName == null || applianceName.isBlank()) {
            applianceName = ApplianceAgent.class.getSimpleName();
        }

        tasks = List.copyOf(tasks);
    }

    @Override
    public AgentParams getAgentParams() {
        return new AgentParams(
                applianceName,
                ApplianceAgent.class,
                new Object[]{
                        householdName,
                        tasks
                });
    }
}
