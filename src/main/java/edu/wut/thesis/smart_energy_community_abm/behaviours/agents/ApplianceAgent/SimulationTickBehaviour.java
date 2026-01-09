package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.discovery.DiscoveryPhaseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.metering.MeteringPhaseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.negotiation.NegotiationPhaseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseFSMBehaviour;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.Phase.*;

public final class SimulationTickBehaviour extends BaseFSMBehaviour {
    public static final int RUNNING = 1;
    public static final int IDLE = 0;

    public SimulationTickBehaviour(ApplianceAgent agent) {
        super(agent);

        registerFirstState(new DiscoveryPhaseBehaviour(agent), DISCOVERY);
        registerState(new MeteringPhaseBehaviour(agent), METERING);
        registerState(new NegotiationPhaseBehaviour(agent), NEGOTIATION);

        registerTransition(DISCOVERY, METERING, RUNNING);
        registerTransition(DISCOVERY, NEGOTIATION, IDLE);
        registerDefaultTransition(METERING, NEGOTIATION);
        registerDefaultTransition(NEGOTIATION, DISCOVERY);
    }
}
