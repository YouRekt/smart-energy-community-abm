package edu.wut.thesis.smart_energy_community_abm.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wut.thesis.smart_energy_community_abm.application.SimulationService;
import edu.wut.thesis.smart_energy_community_abm.domain.ApplianceTask;
import edu.wut.thesis.smart_energy_community_abm.domain.config.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SimulationController.class)
class SimulationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SimulationService simulationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /start should call service and return 200 OK")
    void testStartSimulation() throws Exception {
        mockMvc.perform(post("/start"))
                .andExpect(status().isOk())
                .andExpect(content().string("Simulation started"));

        verify(simulationService).startSimulation();
    }

    @Test
    @DisplayName("POST /stop should call service and return 200 OK")
    void testStopSimulation() throws Exception {
        mockMvc.perform(post("/stop"))
                .andExpect(status().isOk())
                .andExpect(content().string("Simulation stopped"));

        verify(simulationService).stopSimulation();
    }

    @Test
    @DisplayName("POST /config should parse JSON and configure simulation")
    void testConfigValid() throws Exception {
        // 1. Create a valid config object (reusing logic from our Config tests)
        CommunityConfig validConfig = createValidConfig();

        // 2. Perform Request
        mockMvc.perform(post("/config")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validConfig)))
                .andExpect(status().isOk())
                .andExpect(content().string("Simulation configured"));

        // 3. Verify Service Call
        verify(simulationService).configureSimulation(any(CommunityConfig.class));
    }

    @Test
    @DisplayName("POST /config should return 400 Bad Request if body is missing")
    void testConfigMissingBody() throws Exception {
        mockMvc.perform(post("/config")
                        .contentType(MediaType.APPLICATION_JSON))
                // Spring verifies the @RequestBody is present; if not, it throws 400
                .andExpect(status().isBadRequest());
    }

    // --- Helper to construct valid config ---
    private CommunityConfig createValidConfig() {
        BatteryConfig battery = new BatteryConfig(1000.0, 0.5, true);

        GreenEnergySourceConfig source = new GreenEnergySourceConfig(
                100L, 500.0, 50L, 10.0, 0.1, "WindFarm");

        ApplianceTask task = new ApplianceTask("Task1", 0.5, 100, true, 5, 10.0, 0);
        ApplianceConfig appConfig = new ApplianceConfig("Washer", "House1", List.of(task));
        HouseholdConfig house = new HouseholdConfig(List.of(appConfig), "House1");

        PredictionModelConfig predictionConfig = new PredictionModelConfig("MovingAverage", 0.2, 0.0,10);

        return new CommunityConfig(
                battery,
                List.of(source),
                List.of(house),
                "Balanced",
                predictionConfig
        );
    }
}