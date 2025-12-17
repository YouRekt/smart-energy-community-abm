package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase2;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase2.Panic.CollectPostponeRepliesBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase2.Panic.CollectProposalResultBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase2.Panic.HandlePostponeRepliesBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase2.Panic.SendPostponeRequestsBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.PhaseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;

public class HandlePanicBehaviour extends PhaseBehaviour {
    public HandlePanicBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);

        setupSubBehaviours(new Behaviour[]{
                new OneShotBehaviour(agent) {
                    public void action() {
                        agent.log(String.format("--- Handle Panic: Tick %d ---", agent.tick), LogSeverity.INFO, this);
                    }
                },
                new SendPostponeRequestsBehaviour(agent),
                new CollectPostponeRepliesBehaviour(agent),
                new HandlePostponeRepliesBehaviour(agent),
                new CollectProposalResultBehaviour(agent)
        });
    }
}
