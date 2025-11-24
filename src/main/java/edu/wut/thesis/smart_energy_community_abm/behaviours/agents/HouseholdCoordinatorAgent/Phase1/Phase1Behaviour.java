package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase1;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;

import java.util.ArrayList;
import java.util.List;

public class Phase1Behaviour extends SequentialBehaviour {

    public Phase1Behaviour(HouseholdCoordinatorAgent agent) {
        super(agent);

        final List<Behaviour> behaviours = new ArrayList<>();

        behaviours.add(new HealthStatusBehaviour(agent));
        behaviours.add(new ApplianceTickRelayBehaviour(agent));
        behaviours.add(new ProcessApplianceHealthStatusBehaviour(agent));

        setupSubBehaviour(behaviours);
    }

    private void setupSubBehaviour(List<Behaviour> subBehaviours) {
        for (Behaviour subBehaviour : subBehaviours) {
            subBehaviour.setDataStore(getDataStore());
            addSubBehaviour(subBehaviour);
        }
    }
//
//    @Override
//    public int onEnd() {
//        this.reset();
//        myAgent.addBehaviour(this);
//        return super.onEnd();
//    }
}
