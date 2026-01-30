package edu.wut.thesis.smart_energy_community_abm.web;

import edu.wut.thesis.smart_energy_community_abm.application.AnalysisService;
import edu.wut.thesis.smart_energy_community_abm.domain.dto.SimulationAnalysisResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/runs")
@RequiredArgsConstructor
@CrossOrigin
public class AnalysisController {

    private final AnalysisService analysisService;

    @GetMapping("/{runId}/analysis")
    public ResponseEntity<SimulationAnalysisResponse> getRunAnalysis(@PathVariable Long runId) {
        return ResponseEntity.ok(analysisService.analyzeRun(runId));
    }
}