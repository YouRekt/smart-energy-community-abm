package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.metering;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityBatteryAgent;
import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.agents.GreenEnergyAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.TimeoutMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.lang.acl.ACLMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class CollectEnergyStatusBehaviour extends TimeoutMessageHandlerBehaviour {

    private final CommunityCoordinatorAgent agent;
    private final Map<String, Consumer<ACLMessage>> ontologyActions;

    private Double powerProduced;
    private Double currentCharge;

    public CollectEnergyStatusBehaviour(CommunityCoordinatorAgent agent) {
        super(agent, DataStoreKey.Metering.REQUEST_REPLY_BY);
        this.agent = agent;

        ontologyActions = new HashMap<>();
        ontologyActions.put(GreenEnergyAgent.class.getSimpleName(), msg -> powerProduced += Double.parseDouble(msg.getContent()));
        ontologyActions.put(CommunityBatteryAgent.class.getSimpleName(), msg -> currentCharge = Double.parseDouble(msg.getContent()));
    }

    @Override
    public void onStart() {
        super.onStart();
        powerProduced = 0.0;
        currentCharge = -1.0;

        setExpectedResponses(agent.energyAgents.size() + 1);
    }

    @Override
    public int onEnd() {
        getDataStore().put(DataStoreKey.Metering.CURRENT_CHARGE, currentCharge);
        getDataStore().put(DataStoreKey.Metering.POWER_PRODUCED, powerProduced);
        return super.onEnd();
    }

    @Override
    protected void handleInform(ACLMessage msg) {
        if (!isMessageTimely(msg)) {
            agent.log("Received a stale message " + ((msg.getContent() == null) ? "" : msg.getContent()), LogSeverity.WARN, this);
        } else {
            Consumer<ACLMessage> action = ontologyActions.get(msg.getOntology());

            if (action != null) {
                action.accept(msg);
                incrementReceivedCount();
            } else {
                agent.log("Invalid ontology", LogSeverity.ERROR, this);
            }
        }
    }
}
