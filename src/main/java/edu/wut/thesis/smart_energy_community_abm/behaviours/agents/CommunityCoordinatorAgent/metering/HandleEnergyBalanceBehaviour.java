package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.metering;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseFSMBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.OneShotBehaviour;

public final class HandleEnergyBalanceBehaviour extends BaseFSMBehaviour {
    public static final int NO_PANIC = 0;
    public static final int HAS_PANIC = 1;
    private static final String CHECK_PANIC = "check-panic";
    private static final String HANDLE_PANIC = "handle-panic";
    private static final String EXIT = "exit";

    public HandleEnergyBalanceBehaviour(CommunityCoordinatorAgent agent, DataStore dataStore) {
        super(agent);
        setDataStore(dataStore);

        registerFirstState(new CalculateEnergyBalanceBehaviour(agent), CHECK_PANIC);
        registerState(new HandlePanicBehaviour(agent), HANDLE_PANIC);
        registerLastState(new OneShotBehaviour(agent) {
            // TODO: Wrap these kinds of exit behaviours to a simple one utility behaviour (just pass the log in constructor)
            @Override
            public void action() {
                agent.log("Energy balance resolved", LogSeverity.DEBUG, agent);
            }
        }, EXIT);

        registerTransition(CHECK_PANIC, EXIT, NO_PANIC);
        registerTransition(CHECK_PANIC, HANDLE_PANIC, HAS_PANIC);
        registerDefaultTransition(HANDLE_PANIC, EXIT);
    }
}
