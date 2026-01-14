package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.metering;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.metering.Panic.CollectPostponeRepliesBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.metering.Panic.CollectProposalResultBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.metering.Panic.HandlePostponeRepliesBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.metering.Panic.SendPostponeRequestsBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.PhaseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;

public final class HandlePanicBehaviour extends PhaseBehaviour {
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
