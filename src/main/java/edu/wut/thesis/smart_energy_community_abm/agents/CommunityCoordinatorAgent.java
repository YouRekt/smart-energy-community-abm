package edu.wut.thesis.smart_energy_community_abm.agents;

import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.SimulationTickBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.StartNewTickBehaviour;
import edu.wut.thesis.smart_energy_community_abm.util.LogSeverity;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;

import java.util.ArrayList;
import java.util.List;

public class CommunityCoordinatorAgent extends BaseAgent {
    public static final String AGENT_NAME = "CommunityCoordinatorAgent";
    public long tick = 0;
    public short phase = 1;
    public final List<AID> healthyAgents = new ArrayList<>();

    @Override
    protected void setup() {
        super.setup();

        addBehaviour(new StartNewTickBehaviour(this));
        addBehaviour(new SimulationTickBehaviour(this));
    }
}
