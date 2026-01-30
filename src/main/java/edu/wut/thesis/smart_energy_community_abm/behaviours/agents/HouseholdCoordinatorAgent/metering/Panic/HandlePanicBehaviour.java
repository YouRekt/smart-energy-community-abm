package edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.metering.Panic;

import edu.wut.thesis.smart_energy_community_abm.agents.HouseholdCoordinatorAgent;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.BaseFSMBehaviour;
import edu.wut.thesis.smart_energy_community_abm.behaviours.base.LogBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public final class HandlePanicBehaviour extends BaseFSMBehaviour {
    private static final String LOG = "log";
    private static final String SEND_POSTPONE_REQUESTS = "send-postpone-requests";
    private static final String COLLECT_POSTPONE_REPLIES = "collect-postpone-replies";
    private static final String HANDLE_POSTPONE_REPLIES = "handle-postpone-replies";
    private static final String COLLECT_PROPOSAL_RESULT = "collect-proposal-result";
    private static final String EXIT = "exit";

    public HandlePanicBehaviour(HouseholdCoordinatorAgent agent, DataStore dataStore) {
        super(agent);
        setDataStore(dataStore);

        registerFirstState(new LogBehaviour(agent, "--- Handle Panic! ---", LogSeverity.DEBUG), LOG);
        registerState(new SendPostponeRequestsBehaviour(agent), SEND_POSTPONE_REQUESTS);
        registerState(new CollectPostponeRepliesBehaviour(agent), COLLECT_POSTPONE_REPLIES);
        registerState(new HandlePostponeRepliesBehaviour(agent), HANDLE_POSTPONE_REPLIES);
        registerState(new CollectProposalResultBehaviour(agent), COLLECT_PROPOSAL_RESULT);
        registerLastState(new OneShotBehaviour() {
            @Override
            public void action() {

            }
        }, EXIT);

        registerDefaultTransition(LOG, SEND_POSTPONE_REQUESTS);
        registerDefaultTransition(SEND_POSTPONE_REQUESTS, COLLECT_POSTPONE_REPLIES);
        registerDefaultTransition(COLLECT_POSTPONE_REPLIES, HANDLE_POSTPONE_REPLIES);
        registerTransition(HANDLE_POSTPONE_REPLIES, EXIT, ACLMessage.REFUSE);
        registerTransition(HANDLE_POSTPONE_REPLIES, COLLECT_PROPOSAL_RESULT, ACLMessage.PROPOSE);
        registerDefaultTransition(COLLECT_PROPOSAL_RESULT, EXIT);
    }
}
