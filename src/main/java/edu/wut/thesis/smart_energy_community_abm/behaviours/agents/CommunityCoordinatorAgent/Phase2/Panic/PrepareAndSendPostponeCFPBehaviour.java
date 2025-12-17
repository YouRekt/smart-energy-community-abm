package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2.Panic;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.AllocationEntry;
import edu.wut.thesis.smart_energy_community_abm.domain.PostponeRequest;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.*;

import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2.CalculateEnergyBalanceBehaviour.SHORTFALL;

public final class PrepareAndSendPostponeCFPBehaviour extends OneShotBehaviour {
    public static final String CFP_REPLY_BY = "cfp-reply-by";
    public static final String CFP_TARGETS = "cfp-targets";
    private static final long REPLY_BY_DELAY = 300;
    private final CommunityCoordinatorAgent agent;

    public PrepareAndSendPostponeCFPBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void action() {
//        Map<AID, AllocationEntry> tickAllocations = agent.allocations.getOrDefault(agent.tick, Map.of());
        Double shortfall = (Double) getDataStore().get(SHORTFALL);
//
//        if (tickAllocations.isEmpty() || shortfall == null || shortfall <= 0) {
//            getDataStore().put(CFP_TARGETS, new ArrayList<AID>());
//            return;
//        }
//
//        // Prepare: sort by priority (low first)
//        List<PostponeRequest> requests = tickAllocations.values().stream()
//                .map(entry -> new PostponeRequest(
//                        entry.requesterId(),
//                        agent.tick,
//                        entry.grantedEnergy(),
//                        agent.computePriority(entry, agent.tick)
//                ))
//                .sorted(Comparator.comparingDouble(PostponeRequest::priority))
//                .toList();
//
//        // Select targets until we cover shortfall
//        double cumulativeEnergy = 0.0;
//        List<AID> targetHouseholds = new ArrayList<>();
//
//        for (PostponeRequest req : requests) {
//            if (cumulativeEnergy >= shortfall) break;
//            targetHouseholds.add(req.householdId());
//            cumulativeEnergy += req.energyAmount();
//        }
//
//        getDataStore().put(CFP_TARGETS, targetHouseholds);
//
//        if (targetHouseholds.isEmpty()) {
//            agent.log("No households to ask for postponement", LogSeverity.DEBUG, agent);
//            return;
//        }
//
//        // Send CFP
        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
        agent.householdAgents.forEach(cfp::addReceiver);
        cfp.setContent(String.valueOf(shortfall));
        Date replyBy = new Date(System.currentTimeMillis() + REPLY_BY_DELAY);
        cfp.setReplyByDate(replyBy);
        agent.send(cfp);

        getDataStore().put(CFP_REPLY_BY, replyBy);
        agent.log("Sent postpone CFP to households", LogSeverity.DEBUG, agent);
    }
}