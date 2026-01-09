package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.metering;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.PhaseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;

public final class MeteringPhaseBehaviour extends PhaseBehaviour {
    public MeteringPhaseBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);

        setupSubBehaviours(new Behaviour[]{
                new OneShotBehaviour(agent) {
                    public void action() {
                        agent.log("Entering phase 2", LogSeverity.DEBUG, this);
                    }
                },
                new HandleEnergyBalanceBehaviour(agent,getDataStore()), // Handle CommunityCoordinator's panic or request
                                                                        // to report power used
                new RequestApplianceEnergyUsageBehaviour(agent),        // Request Appliances to report their power usage
                new ProcessApplianceEnergyUsageBehaviour(agent),        // Retrieve power used by the appliances
                new RespondToCommunityCoordinatorBehaviour(agent)       // Respond with the power used by appliances
        });
    }
}
