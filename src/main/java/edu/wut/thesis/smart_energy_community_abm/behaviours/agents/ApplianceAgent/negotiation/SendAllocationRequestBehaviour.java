package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.negotiation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.ApplianceTask;
import edu.wut.thesis.smart_energy_community_abm.domain.EnergyRequest;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Negotiation.ALLOCATION_REQUEST_MSG;

public class SendAllocationRequestBehaviour extends OneShotBehaviour {
    private final ApplianceAgent agent;
    private boolean hasMadeRequest = false;

    public SendAllocationRequestBehaviour(ApplianceAgent agent) {
        this.agent = agent;
    }


    @Override
    public void action() {
        final ACLMessage requestMsg = ((ACLMessage) getDataStore().get(ALLOCATION_REQUEST_MSG));
        if (requestMsg == null) {
            agent.log("Allocation request message from Household Coordinator is null/missing", LogSeverity.ERROR, this);
            return;
        }

        ACLMessage reply = requestMsg.createReply();

        List<EnergyRequest> requests = new ArrayList<>();

        // Iterate over all tasks this appliance is responsible for
        for (ApplianceTask task : agent.taskSchedule.keySet()) {
            if (agent.shouldTaskRun(task, agent.tick)) {
                // Task wants to run. Find a suitable slot.
                long proposedStartTick = findFirstAvailableSlot(task);

                if (proposedStartTick != -1) {
                    requests.add(new EnergyRequest(
                            agent.getAID(),
                            proposedStartTick,
                            task.duration(),
                            task.energyPerTick(),
                            task.taskId()
                    ));
                } else {
                    agent.log("Could not find a slot for task: " + task.taskName() + " (duration: " + task.duration() + ")", LogSeverity.WARN, this);
                }
            }
        }

        if (requests.isEmpty()) {
            reply.setPerformative(ACLMessage.REFUSE);
            reply.setContent("No tasks to schedule");
            agent.send(reply);
            agent.log("Sent REFUSE (No tasks needed)", LogSeverity.DEBUG, this);
            hasMadeRequest = false;
        } else {
            reply.setPerformative(ACLMessage.INFORM);
            try {
                ObjectMapper mapper = new ObjectMapper();
                reply.setContent(mapper.writeValueAsString(requests));
                agent.send(reply);
                agent.log("Sent INFORM with " + requests.size() + " allocation requests", LogSeverity.INFO, this);
                hasMadeRequest = true;
            } catch (JsonProcessingException e) {
                agent.log("Failed to serialize EnergyRequests", LogSeverity.ERROR, this);
            }
        }
    }

    private long findFirstAvailableSlot(ApplianceTask task) {
        long currentSearchTick = agent.tick;
        // Limit search horizon to avoid infinite loops if the schedule is packed forever (unlikely but safe)
        // For example, look ahead 2x the period or a fixed constant like 1000 ticks.
        long searchLimit = agent.tick + Math.max(task.period(), ApplianceAgent.MAX_FUTURE_TICKS);

        while (currentSearchTick < searchLimit) {
            long gap = agent.getAvailableGapDuration(currentSearchTick);

            if (gap >= task.duration()) {
                return currentSearchTick;
            }

            // Optimization: If gap is 0 (occupied), jump to the end of the blocking task.
            // If gap is small (e.g. 2, but we need 5), jump forward by gap?
            // Actually, getAvailableGapDuration returns the space FROM currentSearchTick.
            // If it returns 0, it means currentSearchTick is occupied.
            // We should find the next free tick.

            if (gap == 0) {
                // Find next key (start of next task) isn't helpful if we are *inside* a task.
                // We need the END of the current blocking task.
                var entry = agent.timetable.floorEntry(currentSearchTick);
                if (entry != null && entry.getValue().endTick() >= currentSearchTick) {
                    currentSearchTick = entry.getValue().endTick() + 1;
                } else {
                    // Should theoretically not happen if gap returns 0 logic is correct
                    currentSearchTick++;
                }
            } else {
                // We found a gap, but it was too small.
                // The gap ends at (currentSearchTick + gap).
                // The next task starts immediately after.
                // So we can jump to the end of that NEXT task (or just check the next task start).
                // Safest is to jump to the start of the next task (which defines the end of this gap).
                Long nextTaskStart = agent.timetable.higherKey(currentSearchTick);
                if (nextTaskStart != null) {
                    // The gap ends at nextTaskStart. The task at nextTaskStart is blocking us.
                    // We need to jump past that task.
                    currentSearchTick = agent.timetable.get(nextTaskStart).endTick() + 1;
                } else {
                    // This implies gap is MAX_LONG, so we should have returned already.
                    currentSearchTick++;
                }
            }
        }
        return -1;
    }

    @Override
    public int onEnd() {
        return hasMadeRequest ? ACLMessage.INFORM : ACLMessage.REFUSE;
    }
}
