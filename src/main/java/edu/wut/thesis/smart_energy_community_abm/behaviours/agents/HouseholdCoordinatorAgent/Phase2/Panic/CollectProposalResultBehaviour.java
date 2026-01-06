package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase2.Panic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase2.Panic.CollectPostponeRepliesBehaviour.POSTPONE_AGREEMENTS;

public class CollectProposalResultBehaviour extends BaseMessageHandlerBehaviour {
    private final HouseholdCoordinatorAgent agent;
    private boolean received = false;

    public CollectProposalResultBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void onStart() {
        received = false;
    }

    @Override
    public boolean done() {
        return received;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void handleAcceptProposal(ACLMessage msg) {
        received = true;
        final ACLMessage clearTaskRequest = new ACLMessage(ACLMessage.REQUEST);
        final Map<Long, Double> energyClearedPerTick = new HashMap<>();

        // TODO: Reply to Appliances that we acknowledge their decision
        List<AID> proposals = (List<AID>) getDataStore().get(POSTPONE_AGREEMENTS);
        for (AID appliance : proposals) {
            agent.clearCurrentAllocation(appliance).forEach((tick, energy) ->
                    energyClearedPerTick.merge(tick, energy, Double::sum)
            );
            clearTaskRequest.addReceiver(appliance);
        }
        agent.send(clearTaskRequest);

        final ObjectMapper mapper = new ObjectMapper();
        final ACLMessage reply = msg.createReply(ACLMessage.INFORM);

        try {
            reply.setContent(mapper.writeValueAsString(energyClearedPerTick));
            agent.send(reply);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void handleRejectProposal(ACLMessage msg) {
        received = true;
        agent.log("Community Coordinator rejected proposal", LogSeverity.DEBUG, this);
    }
}
