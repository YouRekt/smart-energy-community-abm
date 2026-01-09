package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.GreenEnergyAgent.metering;

import edu.wut.thesis.smart_energy_community_abm.agents.GreenEnergyAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.PhaseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;

public final class MeteringPhaseBehaviour extends PhaseBehaviour {
    public MeteringPhaseBehaviour(GreenEnergyAgent agent) {
        super(agent);

        setupSubBehaviours(new Behaviour[]{
                new OneShotBehaviour(agent) {
                    public void action() {
                        agent.log("Entering phase 2 (Metering)", LogSeverity.DEBUG, this);
                    }
                },
                new ProcessRequestBehaviour(agent)      // Process CommunityCoordinator's request for power and respond
        });
    }
}
