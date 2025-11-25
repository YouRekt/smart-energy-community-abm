package edu.wut.thesis.smart_energy_community_abm.domain;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.interfaces.AgentConfig;

import java.util.ArrayList;
import java.util.List;

public final class ApplianceConfig implements AgentConfig {

    private final String applianceName;
    private final String householdName;
    private final List<ApplianceTask> tasks = new ArrayList<>();

    public ApplianceConfig(String applianceName, String householdName, List<ApplianceTask> tasks) {
        if (householdName == null || householdName.isBlank()) {
            throw new IllegalArgumentException("householdName is null or empty");
        }

        if (tasks == null || tasks.isEmpty()) {
            throw new IllegalArgumentException("tasks is null or empty");
        }

        this.applianceName = (applianceName == null || applianceName.isBlank())
                ? ApplianceAgent.class.getSimpleName()
                : applianceName;

        this.householdName = householdName;
        this.tasks.addAll(tasks);
    }

    @Override
    public AgentParams getAgentParams() {
        //TODO: Add tasks to ApplianceAgent
        return new AgentParams(applianceName, ApplianceAgent.class, new Object[]{householdName});
    }
}
