package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase1;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityBatteryAgent;
import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.agents.GreenEnergyAgent;
import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.LogSeverity;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

// TODO: Wrap these type of timeout message handlers into a common base class to stop repeating functionalities
public final class CollectAgentResponsesBehaviour extends BaseMessageHandlerBehaviour {
    private final CommunityCoordinatorAgent agent;
    private final Map<String, Consumer<AID>> ontologyActions;

    public CollectAgentResponsesBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;

        ontologyActions = new HashMap<>();
        ontologyActions.put(HouseholdCoordinatorAgent.class.getSimpleName(), agent.householdAgents::add);
        ontologyActions.put(GreenEnergyAgent.class.getSimpleName(), agent.energyAgents::add);
        ontologyActions.put(CommunityBatteryAgent.class.getSimpleName(), aid -> agent.batteryAgent = aid);
    }

    // TODO: Check if all agents already replied to speed up the process
    @Override
    public boolean done() {
        Date replyBy = (Date) getDataStore().get(StartNewTickBehaviour.TICK_REPLY_BY);
        return replyBy.before(new Date());
    }

    @Override
    protected void handleConfirm(ACLMessage msg) {
        Date replyBy = (Date) getDataStore().get(StartNewTickBehaviour.TICK_REPLY_BY);

        if (replyBy.after(new Date())) {
            Consumer<AID> action = ontologyActions.get(msg.getOntology());

            if (action != null) {
                action.accept(msg.getSender());
            } else {
                agent.log("Invalid ontology @ Phase1/CollectAgentResponsesBehaviour", LogSeverity.ERROR);
            }
        } else {
            agent.log("Received a stale message " + ((msg.getContent() == null) ? "" : msg.getContent()), LogSeverity.ERROR);
        }
    }

    @Override
    protected void performBlock() {
        Date replyBy = (Date) getDataStore().get(StartNewTickBehaviour.TICK_REPLY_BY);

        block(replyBy.getTime() - System.currentTimeMillis());
    }
}