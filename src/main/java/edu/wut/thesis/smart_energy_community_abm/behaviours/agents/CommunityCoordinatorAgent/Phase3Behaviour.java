package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase3.*;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.PhaseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.LogSeverity;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;

public final class Phase3Behaviour extends PhaseBehaviour {
    public Phase3Behaviour(CommunityCoordinatorAgent agent) {
        super(agent);

        setupSubBehaviours(new Behaviour[]{
                new OneShotBehaviour(agent) {
                    public void action() {
                        agent.log(String.format("--- Phase 3: Tick %d ---", agent.tick), LogSeverity.INFO, this);
                    }
                },
                new AskForRequestsBehaviour(agent),             // Ask Households whether they want to make any requests
                new CollectRequestsBehaviour(agent),            // Collect the requests from Households
                new ProcessRequestsBehaviour(agent),            // Process the Household requests
                new RespondWithProposalBehaviour(agent),        // Respond to households that requested whether they can
                                                                // proceed or they have to change their request
                new CollectModifiedRequestsBehaviour(agent),
                new ProcessModifiedRequestsBehaviour(agent),    // Process the household's modified requests
                new RespondWithFinalDecisionBehaviour(agent),   // Respond to households with the final decision regarding
                                                                // their updated requests
                new CollectFinalResponsesBehaviour(agent),      // Collect the final responses from the households
                new ExecuteRequestsBehaviour(agent),            // Process and execute all requests that have been submitted
        });
    }
}
