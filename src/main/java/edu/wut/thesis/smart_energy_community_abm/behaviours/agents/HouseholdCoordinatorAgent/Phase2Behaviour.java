package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase2.ProcessApplianceEnergyUsageBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase2.ProcessEnergyUsageRequestBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase2.RequestApplianceEnergyUsageBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase2.RespondToCommunityCoordinatorBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.PhaseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.LogSeverity;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;

public final class Phase2Behaviour extends PhaseBehaviour {
    public Phase2Behaviour(HouseholdCoordinatorAgent agent) {
        super(agent);

        setupSubBehaviours(new Behaviour[]{
                new OneShotBehaviour(agent) {
                    public void action() {
                        agent.log("Entering phase 2", LogSeverity.DEBUG, this);
                    }
                },
                // TODO: Implement behaviour that will handle CommunityCoordinator's energyPanic
                new ProcessEnergyUsageRequestBehaviour(agent),         // Handle CommunityCoordinator's request to report power used
                new RequestApplianceEnergyUsageBehaviour(agent),       // Request Appliances to report their power usage
                new ProcessApplianceEnergyUsageBehaviour(agent),       // Retrieve power used by the appliances
                new RespondToCommunityCoordinatorBehaviour(agent)      // Respond with the power used by appliances
        });
    }
}
