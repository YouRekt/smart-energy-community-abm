package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.negotiation;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.PhaseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;

import java.util.concurrent.TimeUnit;

public final class NegotiationPhaseBehaviour extends PhaseBehaviour {
    public NegotiationPhaseBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);

        setupSubBehaviours(new Behaviour[]{
                new OneShotBehaviour(agent) {
                    public void action() {
                        agent.log(String.format("--- Phase 3 [Negotiation]: Tick %d ---", agent.tick), LogSeverity.INFO, this);
                    }
                },
                new RequestAllocationReservationsBehaviour(agent, getDataStore()),
                new OneShotBehaviour(agent) {
                    public void action() {
                        agent.tick++;
//                        // TODO: REMOVE THIS!!!
//                        try {
//                            TimeUnit.SECONDS.sleep(5);
//                        } catch (InterruptedException e) {
//                            throw new RuntimeException(e);
//                        }
                        agent.getPredictedMaxAmount(agent.tick);
                    }
                },
        });
    }
}
