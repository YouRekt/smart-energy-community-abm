package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.negotiation;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Negotiation.ALLOCATION_REQUEST_MSG;

public class SendAllocationRequestBehaviour extends OneShotBehaviour {
    private final ApplianceAgent agent;
    private boolean hasMadeRequest = false;

    public SendAllocationRequestBehaviour(ApplianceAgent agent) {
        this.agent = agent;
    }


    @Override
    public void action() {
        final ACLMessage request = ((ACLMessage) getDataStore().get(ALLOCATION_REQUEST_MSG)).createReply();

        // TODO: Implement logic checking task queue and requesting message to be put into the timetable or refuse, send a List<EnergyRequest>!
        request.setPerformative(ACLMessage.REFUSE);
        hasMadeRequest = false;
        request.setContent("No allocation task");
        agent.send(request);
    }

    @Override
    public int onEnd() {
        return hasMadeRequest ? ACLMessage.INFORM : ACLMessage.REFUSE;
    }
}
