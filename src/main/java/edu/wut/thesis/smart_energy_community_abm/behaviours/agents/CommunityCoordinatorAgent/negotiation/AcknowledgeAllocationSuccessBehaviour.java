package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.negotiation;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.List;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.TransitionKeys.Negotiation.FINISHED;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.TransitionKeys.Negotiation.NEXT_HOUSEHOLD;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Negotiation.AGENT_LIST;

public final class AcknowledgeAllocationSuccessBehaviour extends BaseMessageHandlerBehaviour {
    private boolean receivedMsg = false;
    private List<AID> agentList;

    public AcknowledgeAllocationSuccessBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onStart() {
        receivedMsg = false;
        agentList = (List<AID>) getDataStore().get(AGENT_LIST);
    }

    @Override
    public int onEnd() {
        getDataStore().put(AGENT_LIST, agentList);
        return agentList.isEmpty() ? FINISHED : NEXT_HOUSEHOLD;
    }

    @Override
    public boolean done() {
        return receivedMsg;
    }

    @Override
    protected void handleInform(ACLMessage msg) {
        receivedMsg = true;
        agent.log("Allocation by household " + msg.getSender().getLocalName() + " acknowledged", LogSeverity.DEBUG, this);
        agentList.remove(msg.getSender());
    }
}
