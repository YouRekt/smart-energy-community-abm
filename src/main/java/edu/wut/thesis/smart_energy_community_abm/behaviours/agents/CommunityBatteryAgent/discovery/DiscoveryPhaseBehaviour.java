package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityBatteryAgent.discovery;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityBatteryAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.PhaseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;

public final class DiscoveryPhaseBehaviour extends PhaseBehaviour {
    public DiscoveryPhaseBehaviour(CommunityBatteryAgent agent) {
        super(agent);

        setupSubBehaviours(new Behaviour[]{
                new OneShotBehaviour(agent) {
                    public void action() {
                        agent.log("Entering phase 1 (Discovery)", LogSeverity.DEBUG, this);
                    }
                },
                new ProcessTickBehaviour(agent)    // Receive and respond to TICK message
        });
    }
}
