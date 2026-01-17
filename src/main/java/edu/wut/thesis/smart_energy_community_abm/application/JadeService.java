package edu.wut.thesis.smart_energy_community_abm.application;

import edu.wut.thesis.smart_energy_community_abm.domain.config.*;
import edu.wut.thesis.smart_energy_community_abm.domain.config.AgentConfig;
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

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public final class JadeService implements SimulationService {
    private static final String TOPIC_SERVICE_PATH = "jade.core.messaging.TopicManagementService";
    private static final String MOBILITY_SERVICE_PATH = "jade.core.mobility.AgentMobilityService";
    private static final String NOTIFICATION_SERVICE_PATH = "jade.core.event.NotificationService";

    private static final ExecutorService jadeExecutor = Executors.newCachedThreadPool();
    private static final Logger logger = LoggerFactory.getLogger(JadeService.class);
    private final HashMap<String, Integer> agentCounts = new HashMap<>();
    private ContainerController mainContainer;

    private CommunityConfig config;

    private static ContainerController createContainer(final Runtime runtime, final Profile profile) {
        try {
            logger.info("Main container created");
            return jadeExecutor.submit(() -> runtime.createMainContainer(profile)).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error starting the main container", e);
            throw new RuntimeException(e);
        }
    }

    private static ArrayList getDefaultServices() {
        final Specifier topicSpecifier = new Specifier();
        final Specifier mobilitySpecifier = new Specifier();
        final Specifier notificationSpecifier = new Specifier();
        topicSpecifier.setClassName(TOPIC_SERVICE_PATH);
        topicSpecifier.setArgs(new Object[]{"true"});
        mobilitySpecifier.setClassName(MOBILITY_SERVICE_PATH);
        mobilitySpecifier.setArgs(new Object[]{"true"});
        notificationSpecifier.setClassName(NOTIFICATION_SERVICE_PATH);
        notificationSpecifier.setArgs(new Object[]{"true"});

        ArrayList services = new ArrayList();
        services.add(topicSpecifier);
        services.add(mobilitySpecifier);
        services.add(notificationSpecifier);
        return services;
    }

    public synchronized <T> void runAgent(final Class<T> agentClass, final Object[] args, final String nickname) {
        String effectiveName = Objects.requireNonNullElse(nickname, agentClass.getSimpleName());

        final int id = agentCounts.merge(effectiveName, 1, Integer::sum);
        StringBuilder sb = new StringBuilder(effectiveName);

        if (id > 1) {
            sb.append('[').append(id).append(']');
        }
        final String agentName = sb.toString();

        try {
            final AgentController agent = mainContainer.createNewAgent(agentName, agentClass.getName(), args);
            agent.start();
        } catch (final StaleProxyException e) {
            throw new RuntimeException("Error running agent [" + agentName + "]", e);
        }
    }

    public synchronized void runAgent(final AgentConfig config) {
        final AgentParams ap = config.getAgentParams();

        runAgent(ap.agentClass(), ap.agentArgs(), ap.agentName());
    }

    @Override
    public synchronized void configureSimulation(CommunityConfig communityConfig) throws IllegalStateException {
        if (communityConfig == null) {
            throw new IllegalArgumentException("CommunityConfig cannot be null");
        }

        config = communityConfig;
    }

    private void applyConfig() {
        for (HouseholdConfig hc : config.householdConfigs()) {
            runAgent(hc);
            for (ApplianceConfig ac : hc.applianceConfigs()) {
                runAgent(ac);
            }
        }

        for (GreenEnergySourceConfig gc : config.energySourcesConfigs()) {
            runAgent(gc);
        }

        runAgent(config.batteryConfig());

        runAgent(config);
    }

    @Override
    public synchronized void startSimulation() throws RuntimeException {
        if (mainContainer == null) {
            final Runtime runtime = Runtime.instance();
            final Profile mainProfile = new ProfileImpl();

            mainProfile.setSpecifiers(Profile.SERVICES, getDefaultServices());

            mainContainer = createContainer(runtime, mainProfile);
        }

        applyConfig();
    }

    @Override
    public synchronized void stopSimulation() throws RuntimeException {
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
}
