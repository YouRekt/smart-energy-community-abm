package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase1;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.PhaseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.LogSeverity;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;

public class Phase1Behaviour extends PhaseBehaviour {
    public Phase1Behaviour(HouseholdCoordinatorAgent agent) {
        super(agent);

        setupSubBehaviours(new Behaviour[]{
                new ReceiveTickBehaviour(agent),                // Receive TICK message
                new ApplianceTickRelayBehaviour(agent),         // Pass on the TICK message to the appliances
                new CollectApplianceResponsesBehaviour(agent),  // Collect health responses from appliances
                new HealthStatusBehaviour(agent),               // Respond to CommunityCoordinator with own health check
                new OneShotBehaviour(agent) {
                    public void action() {
                        StringBuilder sb = new StringBuilder();
                        agent.healthyAppliances.forEach((appliance) -> sb.append(appliance.getLocalName()).append(" "));
                        agent.log(sb.toString(), LogSeverity.DEBUG);
                    }
                }
        });
    }
}
