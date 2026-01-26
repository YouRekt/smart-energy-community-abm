package edu.wut.thesis.smart_energy_community_abm.domain.simulation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wut.thesis.smart_energy_community_abm.domain.config.CommunityConfig;
import edu.wut.thesis.smart_energy_community_abm.persistence.SimulationRunRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SimulationState {
    private final SimulationRunRepository runRepository;

    private SimulationRun currentRun;

    @Getter
    @Setter
    private CommunityConfig currentConfig;

    public void startNewRun() {
        final ObjectMapper mapper = new ObjectMapper();

        try {
            currentRun = SimulationRun.builder()
                    .startTime(LocalDateTime.now())
                    .status(SimulationRun.RunStatus.RUNNING)
                    .configJson(mapper.writeValueAsString(currentConfig))
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

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

    public long getCurrentRunRandomSeed() {
        return currentRun != null ? currentConfig.seed() : 0;
    }
}