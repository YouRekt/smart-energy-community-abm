package edu.wut.thesis.smart_energy_community_abm.domain.config;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record TickConfig(
        Long tickAmount,
        String tickUnit
) {

    @JsonIgnore
    public long getTickMultiplier() {
        if (tickAmount == null || tickUnit == null) {
            return 1;
        }

        long multiplier = tickAmount;

        switch (tickUnit) {
            case "minute" -> multiplier *= 60;
            case "hour" -> multiplier *= 3600;
            case "day" -> multiplier *= 86400;
        }

        return multiplier;
    }
}
