package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase2;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseFSMBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.OneShotBehaviour;

public final class HandleEnergyBalanceBehaviour extends BaseFSMBehaviour {
    public static final int NO_PANIC = 0;
    public static final int HAS_PANIC = 1;
    private static final String CHECK_PANIC = "check-panic";
    private static final String HANDLE_PANIC = "handle-panic";
    private static final String EXIT = "exit";

    public HandleEnergyBalanceBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);

        registerFirstState(new CollectEnergyStatusBehaviour(agent), CHECK_PANIC);
        registerState(new HandlePanicBehaviour(agent), HANDLE_PANIC);
        registerLastState(new OneShotBehaviour(agent) {
            @Override
            public void action() {
                agent.log("Energy balance resolved", LogSeverity.DEBUG, agent);
            }
        }, EXIT);

        registerTransition(CHECK_PANIC, EXIT, NO_PANIC);
        registerTransition(CHECK_PANIC, HANDLE_PANIC, HAS_PANIC);
        addTransition(HANDLE_PANIC, CHECK_PANIC);   // After handled panic the CommunityCoordinator should send the REQUEST
                                                    // message so we can reuse the logic in the first Behaviour
    }
}