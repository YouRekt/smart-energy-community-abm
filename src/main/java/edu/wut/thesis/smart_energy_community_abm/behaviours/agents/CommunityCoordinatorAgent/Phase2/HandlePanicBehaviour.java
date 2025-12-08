package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2.Panic.CollectPostponeResponsesBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2.Panic.PrepareAndSendPostponeCFPBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2.Panic.ProcessPostponeResponsesBehaviour;
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
                new PrepareAndSendPostponeCFPBehaviour(agent),  // Prepare + send in one
                new CollectPostponeResponsesBehaviour(agent),   // Async wait for responses
                new ProcessPostponeResponsesBehaviour(agent)    // Recalculate + resolve in one
        });
    }
}
