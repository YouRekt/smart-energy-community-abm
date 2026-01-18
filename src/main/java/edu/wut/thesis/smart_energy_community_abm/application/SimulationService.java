package edu.wut.thesis.smart_energy_community_abm.application;


import edu.wut.thesis.smart_energy_community_abm.domain.config.CommunityConfig;

public interface SimulationService {
    void startSimulation();

    void stopSimulation();

    void configureSimulation(CommunityConfig communityConfig);
}
