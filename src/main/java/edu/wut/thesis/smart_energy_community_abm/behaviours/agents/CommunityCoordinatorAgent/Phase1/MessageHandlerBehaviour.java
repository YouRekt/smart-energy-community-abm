package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase1;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.LogSeverity;
import jade.lang.acl.ACLMessage;

import java.util.Date;

public class MessageHandlerBehaviour extends BaseMessageHandlerBehaviour {
    private final CommunityCoordinatorAgent agent;

    public MessageHandlerBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public boolean done() {
        Date replyBy = (Date) getDataStore().get(StartNewTickBehaviour.TICK_REPLY_BY);

        return replyBy.before(new Date());
    }

    @Override
    protected void handleConfirm(ACLMessage msg) {
        Date replyBy = (Date) getDataStore().get(StartNewTickBehaviour.TICK_REPLY_BY);

        if (replyBy.after(new Date())) {
            agent.healthyAgents.add(msg.getSender());
        } else {
            agent.log("Received a stale message " + ((msg.getContent() == null) ? "" : msg.getContent()), LogSeverity.ERROR);
        }
    }
}
