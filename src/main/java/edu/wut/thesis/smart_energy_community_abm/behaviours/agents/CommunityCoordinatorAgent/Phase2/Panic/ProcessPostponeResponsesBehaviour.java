package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2.Panic;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.AllocationEntry;
import edu.wut.thesis.smart_energy_community_abm.domain.PostponeResponse;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.List;
import java.util.Map;

import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2.CalculateEnergyBalanceBehaviour.SHORTFALL;
import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2.CollectEnergyStatusBehaviour.CURRENT_CHARGE;

public final class ProcessPostponeResponsesBehaviour extends OneShotBehaviour {
    public static final String BATTERY_GRACE_PREFIX = "battery-grace-";
    private final CommunityCoordinatorAgent agent;

    public ProcessPostponeResponsesBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void action() {
        List<PostponeResponse> responses = (List<PostponeResponse>) getDataStore().get(CollectPostponeResponsesBehaviour.POSTPONE_RESPONSES);
        Double shortfall = (Double) getDataStore().get(SHORTFALL);
        Double batteryCharge = (Double) getDataStore().get(CURRENT_CHARGE);

        if (batteryCharge == null) batteryCharge = 0.0;

        // --- Recalculate shortfall after accepted postponements ---
        double freedEnergy = 0.0;

        if (responses != null) {
            for (PostponeResponse resp : responses) {
                if (resp.accepted()) {
                    freedEnergy += resp.energyFreed();
                    agent.removeAllocation(agent.tick, resp.householdId());
                    agent.log("Household " + resp.householdId().getLocalName() + " accepted postpone, freed " + resp.energyFreed(), LogSeverity.DEBUG, agent);
                } else {
                    agent.log("Household " + resp.householdId().getLocalName() + " rejected postpone", LogSeverity.DEBUG, agent);
                }
            }
        }

        double remainingShortfall = (shortfall != null) ? Math.max(0, shortfall - freedEnergy) : 0.0;

        // --- Resolve final shortfall ---
        if (remainingShortfall <= 0) {
            agent.log("Panic resolved after postponements", LogSeverity.DEBUG, agent);
            agent.energyPanic = false;
            return;
        }

        agent.log("Remaining shortfall: " + remainingShortfall + ", checking battery grace", LogSeverity.DEBUG, agent);

        Map<AID, AllocationEntry> remaining = agent.allocations.getOrDefault(agent.tick, Map.of());

        for (Map.Entry<AID, AllocationEntry> entry : remaining.entrySet()) {
            AID householdId = entry.getKey();
            AllocationEntry allocation = entry.getValue();

            if (agent.shouldGrantBatteryGrace(householdId, allocation.grantedEnergy(), remainingShortfall, batteryCharge)) {
                getDataStore().put(BATTERY_GRACE_PREFIX + householdId.getLocalName(), true);
                agent.log("Battery grace granted to " + householdId.getLocalName(), LogSeverity.DEBUG, agent);
            } else {
                ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
                inform.addReceiver(householdId);
                inform.setContent("GRID:" + allocation.grantedEnergy());
                agent.send(inform);
                agent.log("Grid fallback for " + householdId.getLocalName(), LogSeverity.DEBUG, agent);
            }
        }

        agent.energyPanic = false;
    }
}