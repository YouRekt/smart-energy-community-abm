package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseFSMBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.OneShotBehaviour;

import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2.CalculateEnergyBalanceBehaviour.HAS_PANIC;
import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2.CalculateEnergyBalanceBehaviour.NO_PANIC;

public final class HandleEnergyBalanceBehaviour extends BaseFSMBehaviour {
    private static final String CHECK_PANIC = "check-panic";
    private static final String HANDLE_PANIC = "handle-panic";
    private static final String EXIT = "exit";

    public HandleEnergyBalanceBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);

        registerFirstState(new CalculateEnergyBalanceBehaviour(agent), CHECK_PANIC);
        registerState(new HandlePanicBehaviour(agent), HANDLE_PANIC);
        registerLastState(new OneShotBehaviour() {
            @Override
            public void action() {
                agent.log("Exiting panic", LogSeverity.DEBUG, this);
            }
        }, EXIT);

        registerTransition(CHECK_PANIC, EXIT, NO_PANIC);
        registerTransition(CHECK_PANIC, HANDLE_PANIC, HAS_PANIC);
        addTransition(HANDLE_PANIC, EXIT);
    }
}
