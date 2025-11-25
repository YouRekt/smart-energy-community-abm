package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import jade.lang.acl.ACLMessage;

public class ProcessTickStateBehaviour extends BaseMessageHandlerBehaviour {
    public static final String COMMUNITY_BATTERY_AGENT = "CommunityBatteryAgent";
    public static final String GREEN_ENERGY_AGENT = "GreenEnergyAgent";
    public static final String HOUSEHOLD_COORDINATOR_AGENT = "HouseholdCoordinatorAgent";
    private Double currentBalance;
    private final CommunityCoordinatorAgent agent;

    public ProcessTickStateBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void onStart() {
        currentBalance = 0.0;
    }

    @Override
    protected void handleInform(ACLMessage msg) {
        currentBalance += Double.parseDouble(msg.getContent());
    }
}
