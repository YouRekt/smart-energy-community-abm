package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.StartNewTickBehaviour;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;

public class Phase1Behaviour extends SequentialBehaviour {
    public Phase1Behaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        Behaviour b1 = new StartNewTickBehaviour(agent);
        b1.setDataStore(getDataStore());
        addSubBehaviour(b1);

        Behaviour b2 = new ProcessHealthResponses(agent);
        b2.setDataStore(getDataStore());
        addSubBehaviour(b2);
    }
}
