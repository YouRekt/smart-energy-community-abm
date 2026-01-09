package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.discovery;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityBatteryAgent;
import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.agents.GreenEnergyAgent;
import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.TimeoutMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class CollectAgentResponsesBehaviour extends TimeoutMessageHandlerBehaviour {
    private final CommunityCoordinatorAgent agent;
    private final Map<String, Consumer<AID>> ontologyActions;

    public CollectAgentResponsesBehaviour(CommunityCoordinatorAgent agent) {
        super(agent, StartNewTickBehaviour.TICK_REPLY_BY);
        this.agent = agent;

        ontologyActions = new HashMap<>();
        ontologyActions.put(HouseholdCoordinatorAgent.class.getSimpleName(), agent.householdAgents::add);
        ontologyActions.put(GreenEnergyAgent.class.getSimpleName(), agent.energyAgents::add);
        ontologyActions.put(CommunityBatteryAgent.class.getSimpleName(), aid -> agent.batteryAgent = aid);
    }

    @Override
    public void onStart() {
        super.onStart();
        int expected = agent.householdCount + agent.energySourceCount + 1;
        setExpectedResponses(expected);
    }

    @Override
    protected void handleConfirm(ACLMessage msg) {
        if (!isMessageTimely(msg)) {
            agent.log("Received a stale message " + ((msg.getContent() == null) ? "" : msg.getContent()), LogSeverity.WARNING, this);
        } else {
            Consumer<AID> action = ontologyActions.get(msg.getOntology());
            if (action != null) {
                action.accept(msg.getSender());
                incrementReceivedCount();
            } else {
                agent.log("Invalid ontology", LogSeverity.ERROR, this);
            }
        }
    }
}