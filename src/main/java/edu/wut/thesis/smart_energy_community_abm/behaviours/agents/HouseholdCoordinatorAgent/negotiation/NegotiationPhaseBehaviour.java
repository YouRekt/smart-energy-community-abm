package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.negotiation;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.PhaseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;

public final class NegotiationPhaseBehaviour extends PhaseBehaviour {
    public NegotiationPhaseBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);

        setupSubBehaviours(new Behaviour[]{
                new OneShotBehaviour(agent) {
                    public void action() {
                        agent.log("Entering phase 3 (Negotiation)", LogSeverity.DEBUG, this);
                    }
                },
                new CollectCommunityAllocationRequestBehaviour(agent),
                new SendAllocationRequestBehaviour(agent),
                new CollectApplianceAllocationRequestBehaviour(agent),
                new AllocationReservationNegotiationBehaviour(agent, getDataStore()),
        });
    }
}
