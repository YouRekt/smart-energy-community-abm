package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityBatteryAgent;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityBatteryAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityBatteryAgent.Phase2.ProcessPowerUsageBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityBatteryAgent.Phase2.ProcessRequestBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.PhaseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.LogSeverity;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;

public final class Phase2Behaviour extends PhaseBehaviour {
    public Phase2Behaviour(CommunityBatteryAgent agent) {
        super(agent);

        setupSubBehaviours(new Behaviour[]{
                new OneShotBehaviour(agent) {
                    public void action() {
                        agent.log("Entering phase 2", LogSeverity.DEBUG);
                    }
                },
                new ProcessRequestBehaviour(agent),         // Process CommunityCoordinator's request for power and respond
                new ProcessPowerUsageBehaviour(agent)       // Process CommunityCoordinator's information about green energy used
        });
    }
}
