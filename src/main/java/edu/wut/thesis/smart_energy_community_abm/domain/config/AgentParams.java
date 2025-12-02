package edu.wut.thesis.smart_energy_community_abm.domain.config;

public record AgentParams(
        String agentName,
        Class<?> agentClass,
        Object[] agentArgs
) {
}
