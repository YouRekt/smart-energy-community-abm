package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.metering;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseFSMBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.LogBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.DataStore;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.TransitionKeys.Metering.HAS_PANIC;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.TransitionKeys.Metering.NO_PANIC;

public final class HandleEnergyBalanceBehaviour extends BaseFSMBehaviour {
    private static final String CHECK_PANIC = "check-panic";
    private static final String HANDLE_PANIC = "handle-panic";
    private static final String EXIT = "exit";

    public HandleEnergyBalanceBehaviour(CommunityCoordinatorAgent agent, DataStore dataStore) {
        super(agent);
        setDataStore(dataStore);

        registerFirstState(new CalculateEnergyBalanceBehaviour(agent), CHECK_PANIC);
        registerState(new HandlePanicBehaviour(agent, dataStore), HANDLE_PANIC);
        registerLastState(new LogBehaviour(agent, "Energy balance resolved", LogSeverity.DEBUG), EXIT);

        registerTransition(CHECK_PANIC, EXIT, NO_PANIC);
        registerTransition(CHECK_PANIC, HANDLE_PANIC, HAS_PANIC);
        registerDefaultTransition(HANDLE_PANIC, EXIT);
    }
}
