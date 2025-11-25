package edu.wut.thesis.smart_energy_community_abm.domain;

import edu.wut.thesis.smart_energy_community_abm.agents.GreenEnergyAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.interfaces.AgentConfig;

public class GreenEnergySourceConfig implements AgentConfig {

    // Power Stats
    private final Long period;
    private final Double maxOutputPower;
    private final Long mu;
    private final Double sigma;

    private final String agentName;

    public GreenEnergySourceConfig(Long period, Double maxOutputPower, Long mu, Double sigma, String agentName) {
        if (period == null || period <= 0) {
            throw new IllegalArgumentException("period argument is null or negative");
        }

        if (maxOutputPower == null || maxOutputPower <= 0) {
            throw new IllegalArgumentException("maxOutputPower argument is null or negative");
        }

        if (mu == null || mu <= 0) {
            throw new IllegalArgumentException("mu argument is null or negative");
        }

        if (sigma == null || sigma <= 0) {
            throw new IllegalArgumentException("sigma argument is null or negative");
        }

        if (agentName == null || agentName.isBlank()) {
            throw new IllegalArgumentException("agentName argument is null or blank");
        }

        this.period = period;
        this.maxOutputPower = maxOutputPower;
        this.mu = mu;
        this.sigma = sigma;
        this.agentName = agentName;
    }

    @Override
    public AgentParams getAgentParams() {
        return new AgentParams(agentName, GreenEnergyAgent.class, new Object[]{period, maxOutputPower, mu, sigma});
    }
}
