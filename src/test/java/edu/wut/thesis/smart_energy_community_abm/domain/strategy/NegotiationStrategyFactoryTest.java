package edu.wut.thesis.smart_energy_community_abm.domain.strategy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class NegotiationStrategyFactoryTest {

    static Stream<Arguments> strategyProvider() {
        return Stream.of(
                Arguments.of("GreenScoreFirst", GreenScoreFirstStrategy.class),
                Arguments.of("ReservationFirst", ReservationFirstStrategy.class),
                Arguments.of("EnergyVolume", EnergyVolumeStrategy.class),
                Arguments.of("BalancedStrategy", BalancedStrategy.class), // Default case fall-through check
                Arguments.of("UnknownString", BalancedStrategy.class),    // Unknown -> Default
                Arguments.of("", BalancedStrategy.class),                 // Empty -> Default
                Arguments.of(null, BalancedStrategy.class)                // Null -> Default
        );
    }

    @ParameterizedTest(name = "Input '{0}' should return {1}")
    @MethodSource("strategyProvider")
    @DisplayName("Should create correct strategy implementation based on name")
    void testCreateStrategy(String strategyName, Class<? extends NegotiationStrategy> expectedClass) {
        NegotiationStrategy strategy = NegotiationStrategyFactory.create(strategyName);
        assertNotNull(strategy);
        assertInstanceOf(expectedClass, strategy);
    }
}