package edu.wut.thesis.smart_energy_community_abm.agents;

import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.HouseholdCoordinatorAgent.Phase1.Phase1Behaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.LogSeverity;
import jade.core.AID;
import jade.core.ServiceException;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.messaging.TopicManagementHelper;

import java.util.ArrayList;
import java.util.List;

public class HouseholdCoordinatorAgent extends BaseAgent {
    public final List<AID> healthyAppliances = new ArrayList<>();
    public String name;
    public long tick = 0;
    public Integer applianceCount;

    @Override
    protected void setup() {
        super.setup();

        final Object[] args = getArguments();

        name = (String) args[0];

        if (name == null) {
            log("Agent name is missing", LogSeverity.ERROR);
            doDelete();
            throw new RuntimeException("Agent name is missing");
        }

        applianceCount = (Integer) args[1];

        if (applianceCount == null) {
            log("Agent applianceCount is missing", LogSeverity.ERROR);
            doDelete();
            throw new RuntimeException("Agent applianceCount is missing");
        }

        try {
            TopicManagementHelper topicHelper = (TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);
            AID topic = topicHelper.createTopic("TICK");
            topicHelper.register(topic);
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }

        FSMBehaviour test = new FSMBehaviour();
        test.registerFirstState(new Phase1Behaviour(this), "Phase1");

        test.registerState(new OneShotBehaviour() {
            @Override
            public void action() {
                log("My healthy appliances are:", LogSeverity.INFO);
                healthyAppliances.forEach((AID a) -> log(a.getLocalName(), LogSeverity.INFO));
            }
        }, "Dummy");
        test.registerDefaultTransition("Phase1", "Dummy");
        test.registerDefaultTransition("Dummy", "Phase1", new String[]{"Dummy", "Phase1"});

        addBehaviour(test);
    }
}
