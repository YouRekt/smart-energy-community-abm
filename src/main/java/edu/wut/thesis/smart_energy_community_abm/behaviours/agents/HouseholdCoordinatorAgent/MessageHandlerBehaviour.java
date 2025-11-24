package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase1.ApplianceTickRelayBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase1.HealthStatusBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase1.ProcessApplianceHealthStatusBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseMessageHandlerBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.MessageSubject;
import edu.wut.thesis.smart_energy_community_abm.domain.TopicHelper;
import jade.core.AID;
import jade.core.ServiceException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class MessageHandlerBehaviour extends BaseMessageHandlerBehaviour {
    protected final HouseholdCoordinatorAgent agent;
    private final AID topic;

    public MessageHandlerBehaviour(HouseholdCoordinatorAgent agent) {
        super(agent);
        this.agent = agent;

        try {
            topic = TopicHelper.getTopic(agent, "TICK");
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void handleInform(ACLMessage msg) {
        switch (agent.phase) {
            case 1:
                handleInformPhase1(msg);
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            default:
                throw new RuntimeException("invalid phase " + agent.phase);
        }
    }

    @Override
    protected void handleConfirm(ACLMessage msg) {
        switch (agent.phase) {
            case 1:
                handleConfirmPhase1(msg);
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            default:
                throw new RuntimeException("invalid phase " + agent.phase);
        }
    }

    private void handleInformPhase1(ACLMessage msg) {
        MessageTemplate mt = MessageTemplate.MatchTopic(topic);

        if (mt.match(msg)) {
            agent.addBehaviour(new HealthStatusBehaviour(agent, msg));

            ApplianceTickRelayBehaviour relay = new ApplianceTickRelayBehaviour(agent);
            relay.setDataStore(getDataStore());

            agent.addBehaviour(relay);
        }
    }

    private void handleConfirmPhase1(ACLMessage msg) {
        MessageTemplate mt = MessageTemplate.MatchOntology(MessageSubject.APPLIANCE_HEALTH_CHECK);

        if (mt.match(msg)) {
            ProcessApplianceHealthStatusBehaviour process = new ProcessApplianceHealthStatusBehaviour(agent, msg);
            process.setDataStore(getDataStore());

            agent.addBehaviour(process);
        }
    }
}
