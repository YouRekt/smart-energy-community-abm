package edu.wut.thesis.smart_energy_community_abm.domain.simulation;

import edu.wut.thesis.smart_energy_community_abm.persistence.SimulationRunRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

@Component
@RequiredArgsConstructor
public class SimulationState {
    private final SimulationRunRepository runRepository;

    private SimulationRun currentRun;

    public void startNewRun() {
        this.currentRun = SimulationRun.builder()
                .startTime(LocalDateTime.now())
                .status(SimulationRun.RunStatus.RUNNING)
                .build();

        runRepository.save(this.currentRun);
    }

    public void finishRun() {
        if (this.currentRun != null && this.currentRun.getStatus() == SimulationRun.RunStatus.RUNNING) {
            this.currentRun.setEndTime(LocalDateTime.now());
            this.currentRun.setStatus(SimulationRun.RunStatus.COMPLETED);
            runRepository.save(this.currentRun);
        }
    }

    public LocalDateTime getCurrentRunStartTime() {
        return currentRun != null ? currentRun.getStartTime() : null;
    }

    public Long getCurrentRunId() {
        return currentRun != null ? currentRun.getId() : null;
    }
}