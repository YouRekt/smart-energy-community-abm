package edu.wut.thesis.smart_energy_community_abm.domain;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityBatteryAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.interfaces.AgentConfig;

// TODO: Remove minChargeThreshold -> moved to CommunityCoordinatorAgent
public record BatteryConfig(Double capacity, Double minChargeThreshold) implements AgentConfig {
    public BatteryConfig {
        if (capacity == null || capacity <= 0) {
            throw new IllegalArgumentException("capacity argument cannot be null or less than zero");
        }

        if (minChargeThreshold == null || minChargeThreshold <= 0 || minChargeThreshold > 1.0) {
            throw new IllegalArgumentException("minChargeThreshold argument cannot be null or less than zero");
        }

    }

    @Override
    public AgentParams getAgentParams() {
        return new AgentParams(CommunityBatteryAgent.class.getSimpleName(), CommunityBatteryAgent.class, new Object[]{capacity, minChargeThreshold});
    }
}
