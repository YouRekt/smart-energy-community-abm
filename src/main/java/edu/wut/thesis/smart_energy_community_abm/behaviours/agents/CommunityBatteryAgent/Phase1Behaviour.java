package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityBatteryAgent;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityBatteryAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityBatteryAgent.Phase1.ProcessTickBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.PhaseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.LogSeverity;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;

public final class Phase1Behaviour extends PhaseBehaviour {
    public Phase1Behaviour(CommunityBatteryAgent agent) {
        super(agent);

        setupSubBehaviours(new Behaviour[]{
                new OneShotBehaviour(agent) {
                    public void action() {
                        agent.log("Entering phase 1", LogSeverity.DEBUG, this);
                    }
                },
                new ProcessTickBehaviour(agent)    // Receive and respond to TICK message
        });
    }
}
