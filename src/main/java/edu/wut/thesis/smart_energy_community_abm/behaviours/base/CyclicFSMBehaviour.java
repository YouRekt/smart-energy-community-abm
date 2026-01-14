package edu.wut.thesis.smart_energy_community_abm.behaviours.base;

import edu.wut.thesis.smart_energy_community_abm.agents.BaseAgent;
import jade.core.behaviours.Behaviour;

import java.util.List;

/**
 * A generic FSM behaviour that cycles through a provided list of phases indefinitely.
 * <p>
 * Logic: Phase_0 -> Phase_1 -> ... -> Phase_N -> Phase_0
 */
public abstract class CyclicFSMBehaviour extends BaseFSMBehaviour {
    public CyclicFSMBehaviour(BaseAgent agent) {
        super(agent);

        List<Behaviour> phases = getPhases();
        if (phases == null || phases.isEmpty()) {
            throw new IllegalStateException("CyclicFSMBehaviour must have at least one phase.");
        }

        String[] stateNames = new String[phases.size()];

        for (int i = 0; i < phases.size(); i++) {
            String stateName = "phase-" + (i + 1);
            stateNames[i] = stateName;

            if (i == 0) {
                registerFirstState(phases.get(i), stateName);
            } else {
                registerState(phases.get(i), stateName);
            }
        }

        for (int i = 0; i < phases.size(); i++) {
            String current = stateNames[i];
            String next = stateNames[(i + 1) % phases.size()];

            registerDefaultTransition(current, next);
        }
    }

    /**
     * @return An ordered list of behaviours representing the phases of the cycle.
     */
    protected abstract List<Behaviour> getPhases();
}
