package edu.wut.thesis.smart_energy_community_abm.domain.prediction;

public interface EnergyPredictionModel {
    String getName();
    /**
     * Updates the model with the latest observed data from the grid.
     * Should be called once per tick after metering.
     *
     * @param production     Total energy produced by green sources this tick.
     * @param batteryCharge  Current energy stored in the battery.
     */
    void update(double production, double batteryCharge);

    /**
     * Predicts the maximum available energy for the next allocation cycle.
     *
     * @param tick The current simulation tick (useful for time-based/seasonal models).
     * @return The estimated energy amount available for distribution.
     */
    double predictAvailableEnergy(long tick);
}
