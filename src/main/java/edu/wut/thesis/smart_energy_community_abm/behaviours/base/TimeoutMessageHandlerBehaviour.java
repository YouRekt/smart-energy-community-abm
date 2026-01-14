package edu.wut.thesis.smart_energy_community_abm.behaviours.base;

import edu.wut.thesis.smart_energy_community_abm.agents.BaseAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.lang.acl.ACLMessage;

import java.util.Date;

/**
 * A generic behaviour for collecting messages within a specific timeframe.
 * It handles the timeout logic, blocking, and completion checks.
 */
public abstract class TimeoutMessageHandlerBehaviour extends BaseMessageHandlerBehaviour {
    private final String deadlineDataStoreKey;
    private int expectedResponses = -1; // -1 means wait until timeout regardless of count
    private int receivedResponses = 0;

    public TimeoutMessageHandlerBehaviour(BaseAgent agent, String deadlineDataStoreKey) {
        super(agent);
        this.deadlineDataStoreKey = deadlineDataStoreKey;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.receivedResponses = 0;
        this.expectedResponses = -1;
    }

    /**
     * Call this in onStart() if you know how many responses to expect.
     * The behaviour will finish early if this count is reached.
     * @param count the number of messages the behaviour has to receive to terminate early
     */
    protected void setExpectedResponses(int count) {
        this.expectedResponses = count;
    }

    protected void incrementReceivedCount() {
        this.receivedResponses++;
    }

    @Override
    public boolean done() {
        if (expectedResponses != -1 && receivedResponses >= expectedResponses) {
            return true;
        }

        Date deadline = getDeadline();
        return deadline == null || new Date().after(deadline);
    }

    @Override
    protected void performBlock() {
        Date deadline = getDeadline();
        if (deadline != null) {
            long delay = deadline.getTime() - System.currentTimeMillis();
            if (delay > 0) {
                block(delay);
                return;
            }
        }
        block(10);
    }

    /**
     * Checks if the message is timely before processing.
     * @param msg the message to be checked
     * @return {@code true} if message arrived before replyBy, {@code false} otherwise
     */
    protected boolean isMessageTimely(ACLMessage msg) {
        Date deadline = getDeadline();
        if (deadline != null && new Date().after(deadline)) {
            agent.log("Received a stale message: " + (msg.getContent() != null ? msg.getContent() : ""), LogSeverity.WARN, this);
            return false;
        }
        return true;
    }

    private Date getDeadline() {
        Object obj = getDataStore().get(deadlineDataStoreKey);
        if (obj instanceof Date) {
            return (Date) obj;
        }
        return null;
    }
}

