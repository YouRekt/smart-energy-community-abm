package edu.wut.thesis.smart_energy_community_abm.domain.config;

import edu.wut.thesis.smart_energy_community_abm.agents.GreenEnergyAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.config.interfaces.AgentConfig;

public record GreenEnergySourceConfig(
        Long period,
        Double maxOutputPower,
        Long peakTick,
        Double stdDev,
        Double variation,
        String agentName
) implements AgentConfig {

    public GreenEnergySourceConfig {
        if (period == null || period <= 0) {
            throw new IllegalArgumentException("period argument is null or negative");
        }

        if (maxOutputPower == null || maxOutputPower <= 0) {
            throw new IllegalArgumentException("maxOutputPower argument is null or negative");
        }

        if (peakTick == null || peakTick <= 0) {
            throw new IllegalArgumentException("peakTick argument is null or negative");
        }

        if (stdDev == null || stdDev <= 0) {
            throw new IllegalArgumentException("stdDev argument is null or negative");
        }

        if (variation == null || variation <= 0 || variation > 1) {
            throw new IllegalArgumentException("variation argument is null or abs() > 1");
        }

        if (agentName == null || agentName.isBlank()) {
            throw new IllegalArgumentException("agentName argument is null or blank");
        }
    }

    @Override
    public AgentParams getAgentParams() {
        return new AgentParams(agentName,
                GreenEnergyAgent.class,
                new Object[]{
                        period,
                        maxOutputPower,
                        peakTick,
                        stdDev,
                        variation,
                });
    }
}
