package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import edu.wut.thesis.smart_energy_community_abm.agents.BaseAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.discovery.DiscoveryPhaseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.metering.MeteringPhaseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.negotiation.NegotiationPhaseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseFSMBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.CyclicFSMBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.TransitionKeys;
import jade.core.behaviours.Behaviour;

import java.util.List;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.Phase.*;

public final class SimulationTickBehaviour extends CyclicFSMBehaviour {

    public SimulationTickBehaviour(BaseAgent agent) {
        super(agent);
    }

    @Override
    protected List<Behaviour> getPhases() {
        ApplianceAgent agent = (ApplianceAgent) this.myAgent;
        return List.of(
                new DiscoveryPhaseBehaviour(agent),
                new MeteringPhaseBehaviour(agent),
                new NegotiationPhaseBehaviour(agent)
        );
    }
}
