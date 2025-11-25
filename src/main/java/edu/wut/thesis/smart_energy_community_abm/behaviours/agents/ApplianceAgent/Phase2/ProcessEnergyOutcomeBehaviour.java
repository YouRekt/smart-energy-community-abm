package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.Phase2;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import jade.lang.acl.ACLMessage;

public class ProcessEnergyOutcomeBehaviour extends BaseMessageHandlerBehaviour {
    public static final String ALLOWED_GREEN_ENERGY = "allowed-green-energy";
    private final ApplianceAgent agent;
    private boolean msgReceived = false;

    public ProcessEnergyOutcomeBehaviour(ApplianceAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void onStart() {
        msgReceived = false;
    }

    @Override
    protected void handleCfp(ACLMessage msg) {
        msgReceived = true;

        final ACLMessage reply = msg.createReply();
        msg.setOntology(ApplianceAgent.class.getSimpleName());

        if (true) { //TODO: Try to postpone task
            reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
        } else {
            reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            //TODO: Reschedule task internally
        }

        agent.send(reply);

        agent.insufficientEnergy = true;
    }

    @Override
    protected void handleInform(ACLMessage msg) {
        msgReceived = true;
        getDataStore().put(ALLOWED_GREEN_ENERGY, msg);
        agent.insufficientEnergy = false;
    }

    @Override
    public boolean done() {
        return msgReceived;
    }
}
