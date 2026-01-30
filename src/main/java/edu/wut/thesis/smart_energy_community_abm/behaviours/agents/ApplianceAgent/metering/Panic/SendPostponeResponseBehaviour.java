package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.metering.Panic;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import static edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent.FRUSTRATION_THRESHOLD;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Metering.PANIC_CFP;
import static edu.wut.thesis.smart_energy_community_abm.domain.constants.DataStoreKey.Metering.Panic.TASK_TO_POSTPONE;
import static jade.lang.acl.ACLMessage.PROPOSE;
import static jade.lang.acl.ACLMessage.REFUSE;

public final class SendPostponeResponseBehaviour extends OneShotBehaviour {
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

        if (currentTask.task().postponable() && agent.getFrustration() < FRUSTRATION_THRESHOLD) {
            reply.setPerformative(PROPOSE);
            reply.setContent(String.valueOf(currentTask.task().energyPerTick()));
            getDataStore().put(TASK_TO_POSTPONE, currentTask.task());
        } else {
            reply.setPerformative(REFUSE);
            refuse = true;

            if (agent.getFrustration() >= FRUSTRATION_THRESHOLD) {
                agent.log("Refusing to postpone due to high frustration (" + agent.getFrustration() + ")", LogSeverity.DEBUG, this);
            }
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