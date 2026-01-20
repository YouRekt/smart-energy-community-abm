package edu.wut.thesis.smart_energy_community_abm.web;

import edu.wut.thesis.smart_energy_community_abm.application.SimulationService;
import edu.wut.thesis.smart_energy_community_abm.domain.config.CommunityConfig;
import edu.wut.thesis.smart_energy_community_abm.domain.simulation.SimulationState;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/simulation")
@RequiredArgsConstructor
public final class SimulationController {
    private final SimulationService jadeService;
    private final SimulationState simulationState;

    private final AtomicBoolean isConfigured = new AtomicBoolean(false);
    private final AtomicLong randomSeed = new AtomicLong(0L);

    @PostMapping("/start")
    public ResponseEntity<String> startSimulation() {
        if (!isConfigured.get()) {
            return ResponseEntity.badRequest()
                    .body("Error: Configuration missing. Please upload a config before starting.");
        }

        // Finish a run if one is already in progress
        simulationState.finishRun();

        simulationState.startNewRun(randomSeed.get());

        jadeService.startSimulation();
        return ResponseEntity.ok("Started Run ID: " + simulationState.getCurrentRunId());
    }

    @PostMapping("/stop")
    public ResponseEntity<String> stopSimulation() {
        simulationState.finishRun();

        jadeService.stopSimulation();

        return ResponseEntity.ok("Simulation Stopped.");
    }

    @PostMapping("/config")
    public ResponseEntity<String> config(@RequestBody CommunityConfig communityConfig) {
        if (communityConfig == null) {
            return ResponseEntity.badRequest().body("Invalid configuration: request body is missing or malformed.");
        }

        try {
            jadeService.configureSimulation(communityConfig);

            isConfigured.set(true);
            randomSeed.set(communityConfig.seed());

            return ResponseEntity.ok("Simulation configured");
        } catch (Exception e) {
            isConfigured.set(false);
            return ResponseEntity.internalServerError().body("Configuration failed: " + e.getMessage());
        }
    }
}
