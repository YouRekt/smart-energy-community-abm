package edu.wut.thesis.smart_energy_community_abm.behaviours.base;

import edu.wut.thesis.smart_energy_community_abm.agents.BaseAgent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public abstract class SpecificMessageTemplateHandlerBehaviour extends SimpleBehaviour {

    public SpecificMessageTemplateHandlerBehaviour(BaseAgent agent) {
        super(agent);
    }

    @Override
    public void action() {
        final MessageTemplate messageTemplate = getMessageTemplate();

        final ACLMessage msg = myAgent.receive(messageTemplate);

        if (msg != null) {
            handleMatchingMessage(msg);
        } else {
            handleBlock();
        }
    }

    protected void handleBlock() {
        block();
    }

    protected abstract void handleMatchingMessage(ACLMessage msg);

    protected abstract MessageTemplate getMessageTemplate();
}
