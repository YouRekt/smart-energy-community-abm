package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase.Phase1Behaviour;

import edu.wut.thesis.smart_energy_community_abm.domain.LogSeverity;
import jade.core.AID;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;

import java.util.ArrayList;
import java.util.List;

public class SimulationTickBehaviour extends FSMBehaviour {
    private static final String PHASE_1 = "phase-1";
    private final CommunityCoordinatorAgent agent;
    private final List<AID> healthyAgents = new ArrayList<AID>();


    public SimulationTickBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;

        registerFirstState(new Phase1Behaviour(agent), PHASE_1);
        registerLastState(new OneShotBehaviour() {
            @Override
            public void action() {

            }
        }, "Test");
        registerState(new OneShotBehaviour() {
            @Override
            public void action() {
                ((CommunityCoordinatorAgent) myAgent).log("Dummy", LogSeverity.INFO);
            }
        }, "Dummy");
        registerDefaultTransition(PHASE_1, "Dummy");
        registerDefaultTransition("Dummy", PHASE_1, new String[]{"Dummy", PHASE_1});
    }
}