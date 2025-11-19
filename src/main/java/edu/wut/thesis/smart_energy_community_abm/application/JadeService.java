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

    private static ContainerController createContainer(final Runtime runtime, final Profile profile) {
        try {
            logger.info("Main container created");
            return jadeExecutor.submit(() -> runtime.createMainContainer(profile)).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error starting the main container", e);
            throw new RuntimeException(e);
        }
    }

    public synchronized void startContainer() throws RuntimeException {
        if (mainContainer == null) {
            final Runtime runtime = Runtime.instance();
            final Profile mainProfile = new ProfileImpl();

            final Specifier topicSpecifier = new Specifier();
            topicSpecifier.setClassName(TOPIC_SERVICE_PATH);
            topicSpecifier.setArgs(new Object[]{"true"});

            ArrayList services = new ArrayList();
            services.add(topicSpecifier);

            mainProfile.setSpecifiers(Profile.SERVICES, services);

            mainContainer = createContainer(runtime, mainProfile);
        }

        runAgent(ApplianceAgent.class, null);
        runAgent(CommunityCoordinatorAgent.class, null);
    }

    public synchronized void stopContainer() throws RuntimeException {
        if (mainContainer != null) {
            try {
                mainContainer.kill();
                mainContainer = null;
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
        final String agentName = agentClass.getSimpleName() + "[" + System.currentTimeMillis() % 2137 + "]";
        try {
            final AgentController agent = mainContainer.createNewAgent(agentName, agentClass.getName(), args);
            agent.start();
        } catch (final StaleProxyException e) {
            throw new RuntimeException("Error running agent [" + agentName + "]", e);
        }
    }
}
