package edu.wut.thesis.smart_energy_community_abm.web;

import edu.wut.thesis.smart_energy_community_abm.application.interfaces.SimulationService;
import edu.wut.thesis.smart_energy_community_abm.domain.CommunityConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SimulationController {
    private final SimulationService jadeService;

    @PostMapping("/start")
    public ResponseEntity<String> startSimulation() {
        jadeService.startSimulation();
        return ResponseEntity.ok("Simulation started");
    }

    @PostMapping("/stop")
    public ResponseEntity<String> stopSimulation() {
        jadeService.stopSimulation();

        return ResponseEntity.ok("Simulation stopped");
    }

    @PostMapping("/config")
    public ResponseEntity<String> config(@RequestBody CommunityConfig communityConfig) {
        jadeService.configureSimulation(communityConfig);

        return ResponseEntity.ok("Simulation configured");
    }
}
