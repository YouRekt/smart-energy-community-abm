package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.metering;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.metering.Panic.ClearTaskBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.metering.Panic.CollectPostponeResponseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.ApplianceAgent.metering.Panic.SendPostponeResponseBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseFSMBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.OneShotBehaviour;

import static jade.lang.acl.ACLMessage.*;

public final class HandlePanicBehaviour extends BaseFSMBehaviour {
    private static final String SEND_POSTPONE_RESPONSE = "send-postpone-response";
    private static final String COLLECT_POSTPONE_RESPONSE = "collect-postpone-response";
    private static final String EXIT = "exit";
    private static final String CLEAR_TASK = "clear-task";
    private static final String POSTPONE_TASK = "postpone-task";

    public HandlePanicBehaviour(ApplianceAgent agent, DataStore dataStore) {
        super(agent);

        setDataStore(dataStore);

        registerFirstState(new SendPostponeResponseBehaviour(agent), SEND_POSTPONE_RESPONSE);
        registerState(new CollectPostponeResponseBehaviour(agent), COLLECT_POSTPONE_RESPONSE);
        registerState(new ClearTaskBehaviour(agent), CLEAR_TASK);
        registerState(new PostponeTaskBehaviour(agent), POSTPONE_TASK);
        registerLastState(new OneShotBehaviour(agent) {
            @Override
            public void action() {
                agent.log("Leaving HandlePanicBehaviour", LogSeverity.DEBUG, this);
            }
        }, EXIT);

        registerTransition(SEND_POSTPONE_RESPONSE, EXIT, REFUSE);
        registerTransition(SEND_POSTPONE_RESPONSE, COLLECT_POSTPONE_RESPONSE, PROPOSE);
        registerTransition(COLLECT_POSTPONE_RESPONSE, POSTPONE_TASK, REJECT_PROPOSAL);
        registerTransition(COLLECT_POSTPONE_RESPONSE, CLEAR_TASK, ACCEPT_PROPOSAL);
        registerDefaultTransition(POSTPONE_TASK, EXIT);
        registerDefaultTransition(CLEAR_TASK, EXIT);
    }
}
