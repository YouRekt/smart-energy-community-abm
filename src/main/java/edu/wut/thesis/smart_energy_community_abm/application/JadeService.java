package edu.wut.thesis.smart_energy_community_abm.application;

import edu.wut.thesis.smart_energy_community_abm.agents.ApplianceAgent;
import edu.wut.thesis.smart_energy_community_abm.agents.CommunityCoordinatorAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.core.Specifier;
import jade.util.leap.ArrayList;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class JadeService {
    private static final String TOPIC_SERVICE_PATH = "jade.core.messaging.TopicManagementService";
    private static final ExecutorService jadeExecutor = Executors.newCachedThreadPool();
    private static final Logger logger = LoggerFactory.getLogger(JadeService.class);

    private ContainerController mainContainer;
    private ContainerController agentContainer;

    private static ContainerController createContainer(final Runtime runtime, final Profile profile, final boolean mainContainer) {
        try {
            logger.info("{} container created", mainContainer ? "Main" : "Agent");
            return jadeExecutor.submit(() -> mainContainer ? runtime.createMainContainer(profile) : runtime.createAgentContainer(profile)).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error starting the {} container", mainContainer ? "main" : "agent", e);
            throw new RuntimeException(e);
        }
    }

    public synchronized void startContainer() throws RuntimeException {
        final Runtime runtime = Runtime.instance();

        if (mainContainer == null) {
            final Profile mainProfile = new ProfileImpl();
            mainProfile.setParameter(Profile.MAIN, "true");
            mainContainer = createContainer(runtime, mainProfile, true);
        }

        final Profile agentProfile = new ProfileImpl(false);

        Specifier topicSpecifier = new Specifier();
        topicSpecifier.setClassName(TOPIC_SERVICE_PATH);
        topicSpecifier.setArgs(new Object[]{"true"});

        ArrayList services = new ArrayList();
        services.add(topicSpecifier);

        agentProfile.setSpecifiers(Profile.SERVICES, services);

        agentContainer = createContainer(runtime, agentProfile, false);

        runAgent(ApplianceAgent.class, null);
        runAgent(CommunityCoordinatorAgent.class, null);
    }

    public synchronized void stopContainer() throws RuntimeException {
        if (agentContainer != null) {
            try {
                agentContainer.kill();
                agentContainer = null;
                logger.info("Agent container successfully killed.");
            } catch (final Exception e) {
                logger.error("Error stopping the agent container", e);
                throw new RuntimeException("Error stopping agent the container", e);
            }
        } else {
            logger.warn("Agent container not found. Nothing to stop.");
        }
    }

    public synchronized <T> void runAgent(Class<T> agentClass, final Object[] args) {
        final String agentName = agentClass.getSimpleName() + "[" + UUID.randomUUID() + "]";
        try {
            final AgentController agent = agentContainer.createNewAgent(agentName, agentClass.getName(), args);
            agent.start();
        } catch (final StaleProxyException e) {
            throw new RuntimeException("Error running agent [" + agentName + "]", e);
        }
    }
}
