package edu.wut.thesis.smart_energy_community_abm.domain;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityBatteryAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.interfaces.AgentConfig;

public class BatteryConfig implements AgentConfig {
    private final Double capacity;
    private final Double minChargeThreshold;

    public BatteryConfig(Double capacity, Double minChargeThreshold) {
        if (capacity == null || capacity <= 0) {
            throw new IllegalArgumentException("capacity argument cannot be null or less than zero");
        }

        if (minChargeThreshold == null || minChargeThreshold <= 0 || minChargeThreshold > 1.0) {
            throw new IllegalArgumentException("minChargeThreshold argument cannot be null or less than zero");
        }

        this.capacity = capacity;
        this.minChargeThreshold = minChargeThreshold;
    }

    @Override
    public AgentParams getAgentParams() {
        return new AgentParams(CommunityBatteryAgent.class.getSimpleName(), CommunityBatteryAgent.class, new Object[]{capacity, minChargeThreshold});
    }
}
