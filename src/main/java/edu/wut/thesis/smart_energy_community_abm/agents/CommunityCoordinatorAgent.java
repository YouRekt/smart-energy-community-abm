package edu.wut.thesis.smart_energy_community_abm.agents;

import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.SimulationTickBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.StartNewTickBehaviour;
import jade.core.AID;

import java.util.ArrayList;
import java.util.List;

public class CommunityCoordinatorAgent extends BaseAgent {
    public static final String AGENT_NAME = "CommunityCoordinatorAgent";
    public final List<AID> healthyAgents = new ArrayList<>();
    public long tick = 0;
    public short phase = 1;

    @Override
    protected void setup() {
        super.setup();

        addBehaviour(new StartNewTickBehaviour(this));
        addBehaviour(new SimulationTickBehaviour(this));
    }
}
