package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityBatteryAgent;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityBatteryAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityBatteryAgent.discovery.DiscoveryPhaseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityBatteryAgent.metering.MeteringPhaseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.CyclicFSMBehaviour;
import jade.core.behaviours.Behaviour;

import java.util.List;

public final class SimulationTickBehaviour extends CyclicFSMBehaviour {

    public SimulationTickBehaviour(CommunityBatteryAgent agent) {
        super(agent);
    }

    @Override
    protected List<Behaviour> getPhases() {
        CommunityBatteryAgent agent = (CommunityBatteryAgent) this.myAgent;
        return List.of(
                new DiscoveryPhaseBehaviour(agent),
                new MeteringPhaseBehaviour(agent)
        );
    }
}
