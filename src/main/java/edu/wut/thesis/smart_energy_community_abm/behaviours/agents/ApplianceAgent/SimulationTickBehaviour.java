package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseFSMBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.OneShotBehaviour;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.Phase.*;

public final class SimulationTickBehaviour extends BaseFSMBehaviour {
    public static final int RUNNING = 1;
    public static final int IDLE = 0;

    public SimulationTickBehaviour(ApplianceAgent agent) {
        super(agent);

        registerFirstState(new Phase1Behaviour(agent), PHASE_1);
        registerState(new Phase2Behaviour(agent), PHASE_2);
        registerState(new OneShotBehaviour() {
            @Override
            public void action() {
                agent.log("PHASE 3 Placeholder", LogSeverity.DEBUG, this);
            }
        }, PHASE_3);

        registerTransition(PHASE_1, PHASE_2, RUNNING);
        registerTransition(PHASE_1, PHASE_3, IDLE);
        addTransition(PHASE_2, PHASE_3);
        addTransition(PHASE_3, PHASE_1);
    }
}
