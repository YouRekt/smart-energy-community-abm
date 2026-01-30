package edu.wut.thesis.smart_energy_community_abm.persistence;

import edu.wut.thesis.smart_energy_community_abm.domain.simulation.SimulationRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SimulationRunRepository extends JpaRepository<SimulationRun, Long> {
    List<SimulationRun> findAllByOrderByStartTimeDesc();
}