package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.metering.Panic;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey;
import jade.lang.acl.ACLMessage;

import static jade.lang.acl.ACLMessage.*;

// TODO: Make a SingleMessageHandlerBehaviour
public class CollectPostponeResponseBehaviour extends BaseMessageHandlerBehaviour {
    private boolean msgReceived = false;
    private boolean rejected = false;

    public CollectPostponeResponseBehaviour(ApplianceAgent agent) {
        super(agent);
    }

    @Override
    public void onStart() {
        msgReceived = false;
        rejected = false;
    }

    @Override
    protected void handleAcceptProposal(ACLMessage msg) {
        msgReceived = true;
        getDataStore().put(DataStoreKey.Metering.Panic.ACCEPTED_PROPOSAL,msg);
    }

    @Override
    protected void handleRejectProposal(ACLMessage msg) {
        msgReceived = true;
        rejected = true;
    }

    @Override
    public boolean done() {
        return msgReceived;
    }

    @Override
    public int onEnd() {
        return rejected ?
                REJECT_PROPOSAL :
                ACCEPT_PROPOSAL;
    }
}
