package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.MessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.StartNewTickBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Date;

public class ProcessHealthResponses extends MessageHandlerBehaviour {
    public ProcessHealthResponses(CommunityCoordinatorAgent agent) {
        super(agent);
    }

    @Override
    protected void handleConfirm(ACLMessage msg) {
        Date replyBy = (Date) getDataStore().get(StartNewTickBehaviour.TICK_REPLY_BY);

        if (replyBy.after(new Date())) {
            agent.healthyAgents.add(msg.getSender());
        }
        block(replyBy.getTime() - System.currentTimeMillis());
    }

    @Override
    public boolean done() {
        Date replyBy = (Date) getDataStore().get(StartNewTickBehaviour.TICK_REPLY_BY);

        return replyBy.before(new Date());
    }
}
