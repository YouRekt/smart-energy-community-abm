package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityBatteryAgent.metering;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityBatteryAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.PhaseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;

public final class MeteringPhaseBehaviour extends PhaseBehaviour {
    public MeteringPhaseBehaviour(CommunityBatteryAgent agent) {
        super(agent);

        setupSubBehaviours(new Behaviour[]{
                new OneShotBehaviour(agent) {
                    public void action() {
                        agent.log("Entering phase 2", LogSeverity.DEBUG, this);
                    }
                },
                new ProcessRequestBehaviour(agent),         // Process CommunityCoordinator's request for power and respond
                new ProcessEnergyUsageBehaviour(agent)       // Process CommunityCoordinator's information about green energy used
        });
    }
}
