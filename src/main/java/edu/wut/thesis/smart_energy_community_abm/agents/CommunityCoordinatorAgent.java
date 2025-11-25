package edu.wut.thesis.smart_energy_community_abm.agents;

import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.SimulationTickBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.LogSeverity;
import jade.core.AID;

import java.util.ArrayList;
import java.util.List;

public final class CommunityCoordinatorAgent extends BaseAgent {
    public final List<AID> healthyAgents = new ArrayList<>();
    public long tick = 0;
    public short phase = 1;
    public Integer agentCount;

    @Override
    protected void setup() {
        super.setup();

        final Object[] args = getArguments();

        agentCount = (Integer) args[0];

        if (agentCount == null) {
            log("Agent count is missing", LogSeverity.ERROR);
            throw new RuntimeException("Agent count is missing");
        }

        addBehaviour(new SimulationTickBehaviour(this));
    }
}
