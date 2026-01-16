package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.negotiation;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.List;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Negotiation.AGENT_LIST;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Negotiation.HOUSEHOLD_RESPONSE;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.TransitionKeys.Negotiation.FINISHED;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REFUSE;

public final class ProcessHouseholdResponseBehaviour extends BaseMessageHandlerBehaviour {
    private List<AID> agentList;
    private boolean refused = false;
    private boolean msgReceived = false;

    public ProcessHouseholdResponseBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onStart() {
        agentList = (List<AID>) getDataStore().get(AGENT_LIST);
        refused = false;
        msgReceived = false;
    }

    @Override
    protected void handleRefuse(ACLMessage msg) {
        agent.log("Refusal by household " + msg.getSender().getLocalName() + " acknowledged", LogSeverity.DEBUG, this);
        agentList.remove(msg.getSender());
        msgReceived = true;
        refused = true;
    }

    @Override
    protected void handleInform(ACLMessage msg) {
        msgReceived = true;
        getDataStore().put(HOUSEHOLD_RESPONSE, msg);
    }

    @Override
    public boolean done() {
        return msgReceived;
    }

    @Override
    public int onEnd() {
        getDataStore().put(AGENT_LIST, agentList);
        if (agentList.isEmpty()) {
            return FINISHED;
        } else
            return refused ? REFUSE : INFORM;
    }
}
