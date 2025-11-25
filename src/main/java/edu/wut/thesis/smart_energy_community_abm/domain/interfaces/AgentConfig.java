package edu.wut.thesis.smart_energy_community_abm.domain.interfaces;

import edu.wut.thesis.smart_energy_community_abm.domain.AgentParams;

/**
 * Central interface for agent configuration in the Smart Energy Community ABM system.
 * <p>
 * Implementations of this interface should provide the configuration parameters
 * required to initialize and operate an agent within the simulation or application.
 * Typically, each agent type will have its own implementation of this interface,
 * supplying the relevant parameters via the {@link #getAgentParams()} method.
 */
public interface AgentConfig {
    /**
     * Returns the configuration parameters for the agent.
     * <p>
     * Implementations should return an {@link AgentParams} object containing all
     * necessary parameters for the agent's operation, such as behavioral settings,
     * resource limits, or other agent-specific configuration data.
     *
     * @return an {@code AgentParams} instance with the agent's configuration parameters
     */
    AgentParams getAgentParams();
}
