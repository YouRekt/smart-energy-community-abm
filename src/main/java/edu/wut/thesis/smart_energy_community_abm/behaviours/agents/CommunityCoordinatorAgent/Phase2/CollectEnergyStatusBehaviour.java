package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityCoordinatorAgent.Phase2;

import edu.wut.thesis.smart_energy_community_abm.agents.CommunityBatteryAgent;
import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.agents.GreenEnergyAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.LogSeverity;
import jade.lang.acl.ACLMessage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

// TODO: Wrap these type of timeout message handlers into a common base class to stop repeating functionalities
public final class CollectEnergyStatusBehaviour extends BaseMessageHandlerBehaviour {
    public static final String CURRENT_CHARGE = "current-charge";
    public static final String POWER_PRODUCED = "power-produced";
    private final CommunityCoordinatorAgent agent;
    private final Map<String, Consumer<ACLMessage>> ontologyActions;
    private Double powerProduced;
    private Double currentCharge;

    public CollectEnergyStatusBehaviour(CommunityCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;

        ontologyActions = new HashMap<>();
        ontologyActions.put(GreenEnergyAgent.class.getSimpleName(), msg -> powerProduced += Double.parseDouble(msg.getContent()));
        ontologyActions.put(CommunityBatteryAgent.class.getSimpleName(), msg -> currentCharge = Double.parseDouble(msg.getContent()));
    }

    @Override
    public void onStart() {
        powerProduced = 0.0;
        currentCharge = -1.0;
    }

    @Override
    public int onEnd() {
        getDataStore().put(CURRENT_CHARGE, currentCharge);
        getDataStore().put(POWER_PRODUCED, powerProduced);
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
            Consumer<ACLMessage> action = ontologyActions.get(msg.getOntology());

            if (action != null) {
                action.accept(msg);
            } else {
                agent.log("Invalid ontology @ Phase2/CollectEnergyStatusBehaviour", LogSeverity.ERROR, this);
            }
        } else {
            agent.log("Received a stale message " + ((msg.getContent() == null) ? "" : msg.getContent()), LogSeverity.ERROR, this);
        }
    }

    @Override
    protected void performBlock() {
        Date replyBy = (Date) getDataStore().get(RequestEnergyStatusBehaviour.REQUEST_REPLY_BY);

        block(replyBy.getTime() - System.currentTimeMillis());
    }
}
