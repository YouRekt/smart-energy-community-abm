package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase1;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.LogSeverity;
import jade.lang.acl.ACLMessage;

import java.util.Date;

// TODO: Wrap these type of timeout message handlers into a common base class to stop repeating functionalities
public final class CollectApplianceResponsesBehaviour extends BaseMessageHandlerBehaviour {
    private final HouseholdCoordinatorAgent agent;

    public CollectApplianceResponsesBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public boolean done() {
        Date replyBy = (Date) getDataStore().get(ApplianceTickRelayBehaviour.HEALTH_REPLY_BY);
        return replyBy.before(new Date());
    }

    @Override
    protected void handleConfirm(ACLMessage msg) {
        Date replyBy = (Date) getDataStore().get(ApplianceTickRelayBehaviour.HEALTH_REPLY_BY);

        if (replyBy.after(new Date())) {
            agent.healthyAppliances.add(msg.getSender());
        } else {
            agent.log("Received a stale message " + ((msg.getContent() == null) ? "" : msg.getContent()), LogSeverity.ERROR);
        }
    }

    @Override
    protected void performBlock() {
        Date replyBy = (Date) getDataStore().get(ApplianceTickRelayBehaviour.HEALTH_REPLY_BY);

        block(replyBy.getTime() - System.currentTimeMillis());
    }
}