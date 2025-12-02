package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase2;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.LogSeverity;
import jade.lang.acl.ACLMessage;

import java.util.Date;

// TODO: Wrap these type of timeout message handlers into a common base class to stop repeating functionalities
public final class ProcessApplianceEnergyUsageBehaviour extends BaseMessageHandlerBehaviour {
    public static final String GREEN_ENERGY_USED = "green-energy-used";
    public static final String EXTERNAL_ENERGY_USED = "external-energy-used";
    private final HouseholdCoordinatorAgent agent;
    private Double greenEnergyUsed;
    private Double externalEnergyUsed;

    public ProcessApplianceEnergyUsageBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void onStart() {
        greenEnergyUsed = 0.0;
        externalEnergyUsed = 0.0;
    }

    @Override
    public int onEnd() {
        getDataStore().put(GREEN_ENERGY_USED, greenEnergyUsed);
        getDataStore().put(EXTERNAL_ENERGY_USED, externalEnergyUsed);
        return super.onEnd();
    }

    // TODO: Check if all agents already replied to speed up the process
    @Override
    public boolean done() {
        Date replyBy = (Date) getDataStore().get(RequestApplianceEnergyUsageBehaviour.REQUEST_REPLY_BY);
        return replyBy.before(new Date());
    }

    @Override
    protected void handleInform(ACLMessage msg) {
        Date replyBy = (Date) getDataStore().get(RequestApplianceEnergyUsageBehaviour.REQUEST_REPLY_BY);

        if (replyBy.after(new Date())) {
            String[] parts = msg.getContent().split(",");
            greenEnergyUsed += Double.parseDouble(parts[0]);
            externalEnergyUsed += Double.parseDouble(parts[1]);
        } else {
            agent.log("Received a stale message " + ((msg.getContent() == null) ? "" : msg.getContent()), LogSeverity.ERROR, this);
        }
    }

    @Override
    protected void performBlock() {
        Date replyBy = (Date) getDataStore().get(RequestApplianceEnergyUsageBehaviour.REQUEST_REPLY_BY);

        block(replyBy.getTime() - System.currentTimeMillis());
    }
}
