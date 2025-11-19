package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;

public class MessageHandlerBehaviour extends BaseMessageHandlerBehaviour {
    protected final CommunityCoordinatorAgent agent;

    public MessageHandlerBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }
}
