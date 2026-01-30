package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.metering;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.metering.Panic.HandlePanicBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseFSMBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.TransitionKeys;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.OneShotBehaviour;

public final class HandleEnergyBalanceBehaviour extends BaseFSMBehaviour {
    private static final String CHECK_PANIC = "check-panic";
    private static final String HANDLE_PANIC = "handle-panic";
    private static final String EXIT = "exit";

    public HandleEnergyBalanceBehaviour(ApplianceAgent agent, DataStore dataStore) {
        super(agent);

        setDataStore(dataStore);

        registerFirstState(new ProcessEnergyOutcomeBehaviour(agent), CHECK_PANIC);          // Receive information whether we have enough energy to proceed
                                                                                            // or we need to postpone some tasks. Try to postpone current
                                                                                            // task if not possible then send reject to Household coordinator
        registerState(new HandlePanicBehaviour(agent, getDataStore()), HANDLE_PANIC);       // Process Household coordinator's response with green energy
                                                                                            // amount that can be used in this tick, the rest report as grid
        registerLastState(new OneShotBehaviour(agent) {
            @Override
            public void action() {
                agent.log("Energy balance resolved", LogSeverity.DEBUG, agent);
            }
        }, EXIT);

        registerTransition(CHECK_PANIC, EXIT, TransitionKeys.Metering.NO_PANIC);
        registerTransition(CHECK_PANIC, HANDLE_PANIC, TransitionKeys.Metering.HAS_PANIC);
        registerDefaultTransition(HANDLE_PANIC, CHECK_PANIC);
    }
}
