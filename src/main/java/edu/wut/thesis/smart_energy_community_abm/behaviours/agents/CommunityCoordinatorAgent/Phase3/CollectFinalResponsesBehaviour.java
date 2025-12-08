package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase3;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2.RequestEnergyStatusBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// TODO: Wrap these type of timeout message handlers into a common base class to stop repeating functionalities
public final class CollectFinalResponsesBehaviour extends BaseMessageHandlerBehaviour {
    public static final String INITIAL_REQUESTS = "initial-requests";
    // TODO: Change Object to actual request class
    private final List<> initialRequests = new ArrayList<>();
    private final CommunityCoordinatorAgent agent;

    public CollectFinalResponsesBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void onStart() {
        initialRequests.clear();
    }

    @Override
    public int onEnd() {
        getDataStore().put(INITIAL_REQUESTS, initialRequests);
        return super.onEnd();
    }

    // TODO: Check if all agents already replied to speed up the process
    @Override
    public boolean done() {
        Date replyBy = (Date) getDataStore().get(RequestEnergyStatusBehaviour.REQUEST_REPLY_BY);
        return replyBy.before(new Date());
    }

    @Override
    protected void handleInform(ACLMessage msg) {
        Date replyBy = (Date) getDataStore().get(RequestEnergyStatusBehaviour.REQUEST_REPLY_BY);

        if (replyBy.after(new Date())) {
            initialRequests.add(msg.getSender(), );
        }
    }
}
