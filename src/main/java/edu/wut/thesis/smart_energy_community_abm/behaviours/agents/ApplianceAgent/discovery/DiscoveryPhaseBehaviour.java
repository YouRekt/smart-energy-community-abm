package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.discovery;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.PhaseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;

import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.SimulationTickBehaviour.IDLE;
import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.SimulationTickBehaviour.RUNNING;

public final class DiscoveryPhaseBehaviour extends PhaseBehaviour {
    public DiscoveryPhaseBehaviour(ApplianceAgent agent) {
        super(agent);

        setupSubBehaviours(new Behaviour[]{
                new OneShotBehaviour(agent) {
                    public void action() {
                        agent.log("Entering phase 1", LogSeverity.DEBUG, this);
                    }
                },
                new ReceiveTickBehaviour(agent),                // Receive TICK message
        });
    }

    @Override
    public int onEnd() {
        return ((ApplianceAgent) agent).isRunning() ? RUNNING : IDLE;
    }
}
