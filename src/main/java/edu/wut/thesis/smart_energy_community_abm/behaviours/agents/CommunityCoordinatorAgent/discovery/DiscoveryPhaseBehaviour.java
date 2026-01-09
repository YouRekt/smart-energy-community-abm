package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.discovery;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.PhaseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;

public final class DiscoveryPhaseBehaviour extends PhaseBehaviour {
    public DiscoveryPhaseBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);

        setupSubBehaviours(new Behaviour[]{
                new OneShotBehaviour(agent) {
                    public void action() {
                        agent.log(String.format("--- Phase 1: Tick %d ---", agent.tick), LogSeverity.INFO, this);
                    }
                },
                new StartNewTickBehaviour(agent),           // Send TICK message to all agents of interest to get their health
                new CollectAgentResponsesBehaviour(agent)   // Collect health responses from agents
        });
    }
}