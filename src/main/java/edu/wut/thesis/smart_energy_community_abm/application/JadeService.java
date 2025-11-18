package edu.wut.thesis.smart_energy_community_abm.application;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.String.format;

@Service
public class JadeService {
    private static final String agentPackagePath = "edu.wut.thesis.smart_energy_community_abm.agents";
    private static final ExecutorService jadeExecutor = Executors.newCachedThreadPool();
    private static final Logger logger = LoggerFactory.getLogger(JadeService.class);

    private ContainerController container;

    public synchronized void startContainer() throws RuntimeException {
        final Runtime runtime = Runtime.instance();
        final Profile profile = new ProfileImpl();

        if (container == null) {
            try {
                container = jadeExecutor.submit(() -> runtime.createMainContainer(profile)).get();
                logger.info("Container created");
            } catch (final InterruptedException | ExecutionException e) {
                logger.error("Error starting the container", e);
                throw new RuntimeException(e);
            }
        }

        runAgent(agentPackagePath, "ApplianceAgent", null);
    }

    public synchronized void stopContainer() throws RuntimeException {
        if (container != null) {
            try {
                container.kill();
                container = null;
                logger.info("Container successfully killed.");
            } catch (Exception e) {
                logger.error("Error stopping the container", e);
                throw new RuntimeException("Error stopping the container", e);
            }
        }
    }

    public synchronized void runAgent(final String agentPackagePath, final String agentName, final Object[] args) {
        try {
            final String path = format("%s.%s", agentPackagePath, agentName);
            final AgentController agent = container.createNewAgent(agentName, path, args);
            agent.start();
        } catch (final StaleProxyException e) {
            throw new RuntimeException("Error running agent [" + agentName + "]", e);
        }
    }
}
