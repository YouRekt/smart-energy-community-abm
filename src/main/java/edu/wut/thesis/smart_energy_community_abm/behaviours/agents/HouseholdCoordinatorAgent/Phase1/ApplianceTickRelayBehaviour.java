package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase1;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.LogSeverity;
import edu.wut.thesis.smart_energy_community_abm.domain.MessageSubject;
import edu.wut.thesis.smart_energy_community_abm.domain.TopicHelper;
import jade.core.AID;
import jade.core.ServiceException;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Date;

public class ApplianceTickRelayBehaviour extends OneShotBehaviour {
    public final static String HEALTH_REPLY_BY = "health-reply-by";
    public static final int REPLY_BY_DELAY = 100;
    private final AID topic;
    private final HouseholdCoordinatorAgent agent;

    public ApplianceTickRelayBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;

        try {
            this.topic = TopicHelper.getTopic(this.agent, agent.name);
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void action() {
        agent.healthyAppliances.clear();

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(topic);
        msg.setContent(Long.toString(agent.tick));
        msg.setOntology(MessageSubject.APPLIANCE_HEALTH_CHECK);
        msg.setContent(Long.toString(agent.tick));

        Date replyBy = new Date(System.currentTimeMillis() + REPLY_BY_DELAY);

        msg.setReplyByDate(replyBy);
        getDataStore().put(HEALTH_REPLY_BY, replyBy);

        agent.send(msg);

        agent.log("Relayed tick info to my appliances", LogSeverity.INFO);
    }
}
