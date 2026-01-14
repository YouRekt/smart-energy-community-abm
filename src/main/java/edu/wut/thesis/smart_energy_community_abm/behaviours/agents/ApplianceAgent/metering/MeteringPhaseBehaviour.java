package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.metering;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.PhaseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;

public final class MeteringPhaseBehaviour extends PhaseBehaviour {
    public MeteringPhaseBehaviour(ApplianceAgent agent) {
        super(agent);

        setupSubBehaviours(new Behaviour[]{
                new OneShotBehaviour(agent) {
                    public void action() {
                        agent.log("Entering phase 2 (Metering)", LogSeverity.DEBUG, this);
                    }
                },
                new HandleEnergyBalanceBehaviour(agent, getDataStore()),
                new DoWorkBehaviour(agent),                   // Do a unit of work for the current task, report amount of green
                                                              // and grid energy used to Household coordinator and Metrics Database
        });
    }
}
