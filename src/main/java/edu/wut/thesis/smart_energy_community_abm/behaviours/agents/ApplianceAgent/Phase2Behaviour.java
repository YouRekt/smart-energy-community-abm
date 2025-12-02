package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.Phase2.DoWorkBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.Phase2.ProcessEnergyOutcomeBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.Phase2.ProcessResponseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.PhaseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.LogSeverity;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;

public final class Phase2Behaviour extends PhaseBehaviour {
    public Phase2Behaviour(ApplianceAgent agent) {
        super(agent);

        setupSubBehaviours(new Behaviour[]{
                new OneShotBehaviour(agent) {
                    public void action() {
                        agent.log("Entering phase 2", LogSeverity.DEBUG, this);
                    }
                },
                new ProcessEnergyOutcomeBehaviour(agent),   // Receive information whether we have enough energy to proceed
                                                            // or we need to postpone some tasks. Try to postpone current
                                                            // task if not possible then send reject to Household coordinator
                new ProcessResponseBehaviour(agent),        // Process Household coordinator's response with green energy
                                                            // amount that can be used in this tick, the rest report as grid
                new DoWorkBehaviour(agent),                 // Do a unit of work for the current task, report amount of green
                                                            // and grid energy used to Household coordinator and Metrics Database
        });
    }
}
