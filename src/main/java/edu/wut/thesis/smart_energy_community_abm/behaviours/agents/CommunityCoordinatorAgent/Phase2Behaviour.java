package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2.*;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.PhaseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.LogSeverity;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;

public final class Phase2Behaviour extends PhaseBehaviour {
    public Phase2Behaviour(CommunityCoordinatorAgent agent) {
        super(agent);

        setupSubBehaviours(new Behaviour[]{
                new OneShotBehaviour(agent) {
                    public void action() {
                        agent.log("Entering phase 2", LogSeverity.DEBUG);
                    }
                },
                new RequestEnergyStatusBehaviour(agent),        // Send requests to GreenEnergy and CommunityBattery agents
                new CollectEnergyStatusBehaviour(agent),        // Retrieve the amount produced and battery charge
                new CalculateEnergyBalanceBehaviour(agent),     // Calculate the energy balance based on the schedule for this tick
                new InsufficientEnergyPanicBehaviour(agent),    // If previous behaviour sets energyPanic flag to true,
                                                                // this behaviour will attempt to renegotiate to postpone tasks.
                                                                // Ends once energyPanic is false (we have enough energy to satisfy the schedule)
                new RequestEnergyUsageBehaviour(agent),         // Send requests to Households to report their energy usage for the tick
                new ProcessEnergyUsageBehaviour(agent)          // Retrieve the amount of energy used and update the battery
        });
    }
}
