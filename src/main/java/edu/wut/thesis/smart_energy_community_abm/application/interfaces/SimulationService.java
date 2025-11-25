package edu.wut.thesis.smart_energy_community_abm.application.interfaces;


import edu.wut.thesis.smart_energy_community_abm.domain.CommunityConfig;

public interface SimulationService {
    void startSimulation();

    void stopSimulation();

    void configureSimulation(CommunityConfig communityConfig);
}
