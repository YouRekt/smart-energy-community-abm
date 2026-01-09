package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.metering;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.PhaseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;

public final class MeteringPhaseBehaviour extends PhaseBehaviour {
    public MeteringPhaseBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);

        setupSubBehaviours(new Behaviour[]{
                new OneShotBehaviour(agent) {
                    public void action() {
                        agent.log(String.format("--- Phase 2: Tick %d ---", agent.tick), LogSeverity.INFO, this);
                    }
                },
                new RequestEnergyStatusBehaviour(agent),                    // Send requests to GreenEnergy and CommunityBattery agents
                new CollectEnergyStatusBehaviour(agent),                    // Retrieve the amount produced and battery charge
                new HandleEnergyBalanceBehaviour(agent, getDataStore()),    // Calculate the energy balance based on the schedule for this tick
                                                                            // If previous behaviour sets energyPanic flag to true,
                                                                            // this behaviour will attempt to renegotiate to postpone tasks.
                                                                            // Ends once energyPanic is false (we have enough energy to satisfy the schedule)
                new RequestEnergyUsageBehaviour(agent),                     // Send requests to Households to report their energy usage for the tick
                new ProcessEnergyUsageBehaviour(agent),                     // Retrieve the amount of energy used
                new ReportUsageToBatteryBehaviour(agent),                   // Send the amount of green energy used to the battery
                new ProcessBatteryResponseBehaviour(agent),                 // Wait until the battery confirms the update, if the message content is
                                                                            // greater than 0 we have to pull from external grid
        });
    }
}
