package edu.wut.thesis.smart_energy_community_abm.agents;

import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.SimulationTickBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.LogSeverity;
import edu.wut.thesis.smart_energy_community_abm.domain.MessageSubject;
import edu.wut.thesis.smart_energy_community_abm.domain.TopicHelper;
import jade.core.AID;
import jade.core.ServiceException;

import java.util.ArrayList;
import java.util.List;

public final class HouseholdCoordinatorAgent extends BaseAgent {
    public final List<AID> healthyAppliances = new ArrayList<>();
    // TODO: Remove
    public String name;
    public long tick = 0;
    public Integer applianceCount;

    @Override
    protected void setup() {
        super.setup();

        final Object[] args = getArguments();

        name = (String) args[0];

        if (name == null) {
            log("Agent name is missing", LogSeverity.ERROR, this);
            doDelete();
            throw new RuntimeException("Agent name is missing");
        }

        applianceCount = (Integer) args[1];

        if (applianceCount == null) {
            log("Agent applianceCount is missing", LogSeverity.ERROR, this);
            doDelete();
            throw new RuntimeException("Agent applianceCount is missing");
        }

        try {
            TopicHelper.registerTopic(this, MessageSubject.TICK);
        } catch (final ServiceException e) {
            throw new RuntimeException(e);
        }

        addBehaviour(new SimulationTickBehaviour(this));
    }
}
