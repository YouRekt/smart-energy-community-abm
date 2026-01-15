package edu.wut.thesis.smart_energy_community_abm.agents;

import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.SimulationTickBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.AllocationEntry;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import edu.wut.thesis.smart_energy_community_abm.domain.messages.MessageSubject;
import edu.wut.thesis.smart_energy_community_abm.domain.messages.TopicHelper;
import jade.core.AID;
import jade.core.ServiceException;

import java.util.*;

public final class HouseholdCoordinatorAgent extends BaseAgent {
    public final List<AID> healthyAppliances = new ArrayList<>();
    public final TreeMap<Long, Map<AID, AllocationEntry>> timetable = new TreeMap<>();
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

    public double getAllocatedEnergyFor(long tick, AID appliance) {
        return timetable
                .getOrDefault(tick, Map.of())
                .getOrDefault(appliance, new AllocationEntry(0.0, 0, 0, 0))
                .requestedEnergy();
    }

    public Map<Long, Double> clearCurrentAllocation(AID agent) {
        final Map<Long, Double> energyClearedPerTick = new HashMap<>();

        if (!timetable.containsKey(tick) || !timetable.get(tick).containsKey(agent)) {
            return energyClearedPerTick;
        }

        AllocationEntry entry = timetable.get(tick).get(agent);
        double energyValue = entry.requestedEnergy();

        long endTick = entry.allocationEnd();

        for (long t = tick; t <= endTick; t++) {
            if (timetable.containsKey(t) && timetable.get(t).containsKey(agent)) {
                timetable.get(t).remove(agent);
                energyClearedPerTick.put(t, energyValue);
            }
        }

        return energyClearedPerTick;
    }
}
