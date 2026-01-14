package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.metering;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.metering.Panic.CollectAndFinalizePostponeResponsesBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.metering.Panic.CollectPostponeResponsesBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.metering.Panic.PrepareAndSendPostponeCFPBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.metering.Panic.ProcessPostponeResponsesBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.PhaseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;

public class HandlePanicBehaviour extends PhaseBehaviour {
    public HandlePanicBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        setupSubBehaviours(new Behaviour[]{
                new OneShotBehaviour(agent) {
                    public void action() {
                        agent.log(String.format("--- Handle Panic: Tick %d ---", agent.tick), LogSeverity.INFO, this);
                    }
                },
                new PrepareAndSendPostponeCFPBehaviour(agent),          // Prepare + send in one
                new CollectPostponeResponsesBehaviour(agent),           // Async wait for responses
                new ProcessPostponeResponsesBehaviour(agent),           // Go through proposals and accept them until we satisfy the shortfall
                                                                        // , send accept-proposal and reject-proposal
                new CollectAndFinalizePostponeResponsesBehaviour(agent)
                // TODO: Handle cleaning the timetable +
        });
    }
}
