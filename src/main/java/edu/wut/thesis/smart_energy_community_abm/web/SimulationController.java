package edu.wut.thesis.smart_energy_community_abm.web;

import edu.wut.thesis.smart_energy_community_abm.application.JadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SimulationController {
    private final JadeService jadeService;

    @PostMapping("/start")
    public ResponseEntity<String> startSimulation() {
        jadeService.startContainer();

        return ResponseEntity.ok("Simulation started");
    }

    @PostMapping("/stop")
    public ResponseEntity<String> stopSimulation() {
        jadeService.stopContainer();

        return ResponseEntity.ok("Simulation stopped");
    }
}
