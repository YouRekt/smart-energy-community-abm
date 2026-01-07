package edu.wut.thesis.smart_energy_community_abm.agents;

import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.SimulationTickBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.AllocationEntry;
import edu.wut.thesis.smart_energy_community_abm.domain.EnergyRequest;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import edu.wut.thesis.smart_energy_community_abm.domain.messages.MessageSubject;
import edu.wut.thesis.smart_energy_community_abm.domain.messages.TopicHelper;
import jade.core.AID;
import jade.core.ServiceException;

import java.util.*;

public final class HouseholdCoordinatorAgent extends BaseAgent {
    public final List<AID> healthyAppliances = new ArrayList<>();
    public final TreeMap<Long, Map<AID, AllocationEntry>> timetable = new TreeMap<>();
    public final List<EnergyRequest> pendingRequests = new ArrayList<>();
    // TODO: Remove
    public String name;
    public long tick = 0;
    public Integer applianceCount;

    @Override
    protected void setup() {
        super.setup();

        final Object[] args = getArguments();

        applianceCount = (Integer) args[0];

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

    public Map<Long, Double> clearCurrentAllocation(AID agent) {
        final Map<Long, Double> energyClearedPerTick = new HashMap<>();

        for (long t = tick; t <= timetable.get(tick).get(agent).allocationEnd(); t++) {
            timetable.get(t).remove(agent);
            energyClearedPerTick.put(tick, timetable.get(tick).get(agent).requestedEnergy());
        }


        return energyClearedPerTick;
    }
}
