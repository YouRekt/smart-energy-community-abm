package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.metering.Panic;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import static edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.metering.ProcessEnergyOutcomeBehaviour.PANIC_CFP;
import static jade.lang.acl.ACLMessage.PROPOSE;
import static jade.lang.acl.ACLMessage.REFUSE;

public class SendPostponeResponseBehaviour extends OneShotBehaviour {
    private final ApplianceAgent agent;
    private boolean refuse = false;

    public SendPostponeResponseBehaviour(ApplianceAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void onStart() {
        refuse = false;
    }

    @Override
    public void action() {
        final ACLMessage message = (ACLMessage) getDataStore().get(PANIC_CFP);

        final ACLMessage reply = message.createReply();

        var currentTask = agent.timetable.get(agent.tick);

        if (currentTask.task().postponable()) { //TODO: Try to postpone task
            reply.setPerformative(PROPOSE);
            reply.setContent(String.valueOf(currentTask.task().energyPerTick()));
            //TODO: Reschedule task internally -> remember to ask for it in phase 3 -> add it to accepted tasks?
        } else {
            reply.setPerformative(REFUSE);
            refuse = true;
        }

        agent.send(reply);
    }

    @Override
    public int onEnd() {
        return refuse ?
                REFUSE :
                PROPOSE;
    }
}
