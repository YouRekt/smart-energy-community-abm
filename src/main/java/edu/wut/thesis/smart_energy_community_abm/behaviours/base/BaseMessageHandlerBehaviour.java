package edu.wut.thesis.smart_energy_community_abm.behaviours.base;

import edu.wut.thesis.smart_energy_community_abm.agents.BaseAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import edu.wut.thesis.smart_energy_community_abm.domain.util.ACLPerformativeConverter;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

public abstract class BaseMessageHandlerBehaviour extends SimpleBehaviour {
    protected final BaseAgent agent;

    public BaseMessageHandlerBehaviour(BaseAgent agent) {
        this.agent = agent;
        super(agent);
    }

    @Override
    public final void action() {
        final ACLMessage msg = agent.receive();
        if (msg != null) {
            agent.log("Received message: [" + ACLPerformativeConverter.ConvertACLPerformativeToString(msg.getPerformative()) + "] " + (msg.getContent() == null ? "null" : msg.getContent()), LogSeverity.DEBUG, this);
            processMsg(msg);
        } else {
            performBlock();
        }
    }

    @Override
    public boolean done() {
        return false;
    }

    @Override
    public int onEnd() {
        restart();

        return super.onEnd();
    }

    protected void performBlock() {
        block();
    }

    private void processMsg(ACLMessage msg) {
        switch (msg.getPerformative()) {
            case ACLMessage.ACCEPT_PROPOSAL -> handleAcceptProposal(msg);
            case ACLMessage.AGREE -> handleAgree(msg);
            case ACLMessage.CANCEL -> handleCancel(msg);
            case ACLMessage.CFP -> handleCfp(msg);
            case ACLMessage.CONFIRM -> handleConfirm(msg);
            case ACLMessage.DISCONFIRM -> handleDisconfirm(msg);
            case ACLMessage.FAILURE -> handleFailure(msg);
            case ACLMessage.INFORM -> handleInform(msg);
            case ACLMessage.INFORM_IF -> handleInformIf(msg);
            case ACLMessage.INFORM_REF -> handleInformRef(msg);
            case ACLMessage.NOT_UNDERSTOOD -> handleNotUnderstood(msg);
            case ACLMessage.PROPAGATE -> handlePropagate(msg);
            case ACLMessage.PROPOSE -> handlePropose(msg);
            case ACLMessage.PROXY -> handleProxy(msg);
            case ACLMessage.QUERY_IF -> handleQueryIf(msg);
            case ACLMessage.QUERY_REF -> handleQueryRef(msg);
            case ACLMessage.REFUSE -> handleRefuse(msg);
            case ACLMessage.REJECT_PROPOSAL -> handleRejectProposal(msg);
            case ACLMessage.REQUEST -> handleRequest(msg);
            case ACLMessage.REQUEST_WHEN -> handleRequestWhen(msg);
            case ACLMessage.REQUEST_WHENEVER -> handleRequestWhenever(msg);
            case ACLMessage.SUBSCRIBE -> handleSubscribe(msg);
            default -> handleUnknown(msg);
        }
    }

    protected void handleAcceptProposal(ACLMessage msg) {
        agent.log("Not handled: handleAcceptProposal [" + (msg.getContent() == null ? "null" : msg.getContent()) + "]", LogSeverity.INFO, this);
    }

    protected void handleAgree(ACLMessage msg) {
        agent.log("Not handled: handleAgree [" + (msg.getContent() == null ? "null" : msg.getContent()) + "]", LogSeverity.INFO, this);
    }

    protected void handleCancel(ACLMessage msg) {
        agent.log("Not handled: handleCancel [" + (msg.getContent() == null ? "null" : msg.getContent()) + "]", LogSeverity.INFO, this);
    }

    protected void handleCfp(ACLMessage msg) {
        agent.log("Not handled: handleCfp [" + (msg.getContent() == null ? "null" : msg.getContent()) + "]", LogSeverity.INFO, this);
    }

    protected void handleConfirm(ACLMessage msg) {
        agent.log("Not handled: handleConfirm [" + (msg.getContent() == null ? "null" : msg.getContent()) + "]", LogSeverity.INFO, this);
    }

    protected void handleDisconfirm(ACLMessage msg) {
        agent.log("Not handled: handleDisconfirm [" + (msg.getContent() == null ? "null" : msg.getContent()) + "]", LogSeverity.INFO, this);
    }

    protected void handleFailure(ACLMessage msg) {
        agent.log("Not handled: handleFailure [" + (msg.getContent() == null ? "null" : msg.getContent()) + "]", LogSeverity.INFO, this);
    }

    protected void handleInform(ACLMessage msg) {
        agent.log("Not handled: handleInform [" + (msg.getContent() == null ? "null" : msg.getContent()) + "]", LogSeverity.INFO, this);
    }

    protected void handleInformIf(ACLMessage msg) {
        agent.log("Not handled: handleInformIf [" + (msg.getContent() == null ? "null" : msg.getContent()) + "]", LogSeverity.INFO, this);
    }

    protected void handleInformRef(ACLMessage msg) {
        agent.log("Not handled: handleInformRef [" + (msg.getContent() == null ? "null" : msg.getContent()) + "]", LogSeverity.INFO, this);
    }

    protected void handleNotUnderstood(ACLMessage msg) {
        agent.log("Not handled: handleNotUnderstood [" + (msg.getContent() == null ? "null" : msg.getContent()) + "]", LogSeverity.INFO, this);
    }

    protected void handlePropose(ACLMessage msg) {
        agent.log("Not handled: handlePropose [" + (msg.getContent() == null ? "null" : msg.getContent()) + "]", LogSeverity.INFO, this);
    }

    protected void handleQueryIf(ACLMessage msg) {
        agent.log("Not handled: handleQueryIf [" + (msg.getContent() == null ? "null" : msg.getContent()) + "]", LogSeverity.INFO, this);
    }

    protected void handleQueryRef(ACLMessage msg) {
        agent.log("Not handled: handleQueryRef [" + (msg.getContent() == null ? "null" : msg.getContent()) + "]", LogSeverity.INFO, this);
    }

    protected void handleRefuse(ACLMessage msg) {
        agent.log("Not handled: handleRefuse [" + (msg.getContent() == null ? "null" : msg.getContent()) + "]", LogSeverity.INFO, this);
    }

    protected void handleRejectProposal(ACLMessage msg) {
        agent.log("Not handled: handleRejectProposal [" + (msg.getContent() == null ? "null" : msg.getContent()) + "]", LogSeverity.INFO, this);
    }

    protected void handleRequest(ACLMessage msg) {
        agent.log("Not handled: handleRequest [" + (msg.getContent() == null ? "null" : msg.getContent()) + "]", LogSeverity.INFO, this);
    }

    protected void handleRequestWhen(ACLMessage msg) {
        agent.log("Not handled: handleRequestWhen [" + (msg.getContent() == null ? "null" : msg.getContent()) + "]", LogSeverity.INFO, this);
    }

    protected void handleRequestWhenever(ACLMessage msg) {
        agent.log("Not handled: handleRequestWhenever [" + (msg.getContent() == null ? "null" : msg.getContent()) + "]", LogSeverity.INFO, this);
    }

    protected void handleSubscribe(ACLMessage msg) {
        agent.log("Not handled: handleSubscribe [" + (msg.getContent() == null ? "null" : msg.getContent()) + "]", LogSeverity.INFO, this);
    }

    protected void handleProxy(ACLMessage msg) {
        agent.log("Not handled: handleProxy [" + (msg.getContent() == null ? "null" : msg.getContent()) + "]", LogSeverity.INFO, this);
    }

    protected void handlePropagate(ACLMessage msg) {
        agent.log("Not handled: handlePropagate [" + (msg.getContent() == null ? "null" : msg.getContent()) + "]", LogSeverity.INFO, this);
    }

    protected void handleUnknown(ACLMessage msg) {
        agent.log("Not handled: handleUnknown [" + (msg.getContent() == null ? "null" : msg.getContent()) + "]", LogSeverity.INFO, this);
    }
}