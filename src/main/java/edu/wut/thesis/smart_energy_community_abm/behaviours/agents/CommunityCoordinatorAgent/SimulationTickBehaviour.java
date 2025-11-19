package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase.Phase1Behaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.LogSeverity;
import jade.core.AID;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;

public class SimulationTickBehaviour extends FSMBehaviour {
    private static final String PHASE_1 = "phase-1";

    public SimulationTickBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);

        registerFirstState(new Phase1Behaviour(agent), PHASE_1);

        registerState(new OneShotBehaviour() {
            @Override
            public void action() {
                agent.healthyAgents.forEach((AID a) -> agent.log(a.getLocalName(), LogSeverity.ERROR));
            }
        }, "Dummy");
        registerDefaultTransition(PHASE_1, "Dummy");
        registerDefaultTransition("Dummy", PHASE_1, new String[]{"Dummy", PHASE_1});
    }
}