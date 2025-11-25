package edu.wut.thesis.smart_energy_community_abm.agents;

import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.GreenEnergyAgent.SimulationTickBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.MessageSubject;
import edu.wut.thesis.smart_energy_community_abm.domain.TopicHelper;
import jade.core.ServiceException;

import java.util.Random;

public final class GreenEnergyAgent extends BaseAgent {
    private final Random random = new Random();
    public long tick;
    private Long period;
    private Double maxOutputPower;
    private Long peakTick;
    private Double stdDev;
    private Double variation;

    @Override
    protected void setup() {
        super.setup();

        final Object[] args = getArguments();

        period = (Long) args[0];

        if (period == null) {
            throw new IllegalArgumentException("Argument 'period' is required");
        }

        maxOutputPower = (Double) args[1];

        if (maxOutputPower == null) {
            throw new IllegalArgumentException("Argument 'maxOutputPower' is required");
        }

        peakTick = (Long) args[2];

        if (peakTick == null) {
            throw new IllegalArgumentException("Argument 'peakTick' is required");
        }

        stdDev = (Double) args[3];

        if (stdDev == null) {
            throw new IllegalArgumentException("Argument 'stdDev' is required");
        }

        variation = (Double) args[4];

        if (variation == null) {
            throw new IllegalArgumentException("Argument 'variation' is required");
        }

        try {
            TopicHelper.registerTopic(this, MessageSubject.TICK);
        } catch (final ServiceException e) {
            throw new RuntimeException(e);
        }

        addBehaviour(new SimulationTickBehaviour(this));
    }

    public Double produceEnergy(boolean shouldAddRandomNoise) {
        // Get position within the current period
        final long position = tick % period;

        // Calculate Gaussian bell curve centered at peakTick
        // using periodic distance to handle wraparound
        final long distance = Math.min(
                Math.abs(position - peakTick),
                period - Math.abs(position - peakTick)
        );

        // Gaussian bell curve
        final double bellValue = Math.exp(-0.5 * Math.pow(distance / stdDev, 2));
        double production = maxOutputPower * bellValue;

        // Add ±10% random variation
        if (shouldAddRandomNoise) {
            final double noise = 1.0 + (random.nextGaussian() * variation);
            production *= Math.max(0, noise);
        }

        return production;
    }
}