package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.metering.Panic;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.AllocationEntry;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Metering.PANIC_CFP;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Metering.Panic.POSTPONE_AGREEMENTS;

public final class HandlePostponeRepliesBehaviour extends OneShotBehaviour {
    private final HouseholdCoordinatorAgent agent;

    public HandlePostponeRepliesBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void action() {
        List<AID> postponeAgreements = (List<AID>) getDataStore().get(POSTPONE_AGREEMENTS);

        Map<AID, AllocationEntry> currentTickAllocations = agent.timetable.get(agent.tick);

        double totalFreedEnergy = postponeAgreements.stream()
                .map(currentTickAllocations::get)
                .filter(Objects::nonNull)
                .mapToDouble(AllocationEntry::requestedEnergy)
                .sum();

        ACLMessage cfp = (ACLMessage) getDataStore().get(PANIC_CFP);
        ACLMessage reply = cfp.createReply();

        if (totalFreedEnergy > 0) {
            reply.setPerformative(ACLMessage.PROPOSE);
            reply.setContent(String.valueOf(totalFreedEnergy));
        } else {
            reply.setPerformative(ACLMessage.REFUSE);
        }

        agent.send(reply);
    }
}
