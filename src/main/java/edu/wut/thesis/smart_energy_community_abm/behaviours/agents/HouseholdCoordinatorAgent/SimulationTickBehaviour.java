package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.discovery.DiscoveryPhaseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.metering.MeteringPhaseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.negotiation.NegotiationPhaseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.CyclicFSMBehaviour;
import jade.core.behaviours.Behaviour;

import java.util.List;

public final class SimulationTickBehaviour extends CyclicFSMBehaviour {

    public SimulationTickBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);
    }

    @Override
    protected List<Behaviour> getPhases() {
        HouseholdCoordinatorAgent agent = (HouseholdCoordinatorAgent) this.myAgent;
        return List.of(
                new DiscoveryPhaseBehaviour(agent),
                new MeteringPhaseBehaviour(agent),
                new NegotiationPhaseBehaviour(agent)
        );
    }
}