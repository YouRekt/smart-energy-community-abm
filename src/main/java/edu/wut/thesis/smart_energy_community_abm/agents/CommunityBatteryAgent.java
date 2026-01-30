package edu.wut.thesis.smart_energy_community_abm.agents;

import edu.wut.thesis.smart_energy_community_abm.behaviours.agents.CommunityBatteryAgent.SimulationTickBehaviour;
import edu.wut.thesis.smart_energy_community_abm.domain.messages.MessageSubject;
import edu.wut.thesis.smart_energy_community_abm.domain.messages.TopicHelper;
import jade.core.ServiceException;

import java.util.Objects;

import static edu.wut.thesis.smart_energy_community_abm.domain.util.MetricNameHelper.*;

public final class CommunityBatteryAgent extends BaseAgent {
    public Double maxCapacity;
    public Double currentCharge;

    @Override
    protected void setup() {
        super.setup();

        final Object[] args = getArguments();

        maxCapacity = (Double) args[0];

        if (maxCapacity == null) {
            throw new IllegalArgumentException("Capacity parameter is null");
        }

        currentCharge = (Double) args[1];

        if (currentCharge == null) {
            throw new IllegalArgumentException("Current charge parameter is null");
        }

        try {
            TopicHelper.registerTopic(this, MessageSubject.TICK);
        } catch (final ServiceException e) {
            throw new RuntimeException(e);
        }

        addBehaviour(new SimulationTickBehaviour(this));
    }

    public void pushCurrentCharge() {
        pushMetric(BATTERY_CHARGE, currentCharge);
    }

    public void pushDischargeAmount(double dischargeAmount) {
        pushMetric(BATTERY_DISCHARGE_AMOUNT, dischargeAmount);
    }

    public void pushChargeAmount(Double chargeAmount) {
        pushMetric(BATTERY_CHARGE_AMOUNT, chargeAmount);
    }

    public void pushCurtailed(double wasted) {
            pushMetric(BATTERY_CURTAILED, wasted);
    }

    public void pushFull() {
        pushMetric(BATTERY_FULL, 1);
    }

    public void pushEmpty() {
        pushMetric(BATTERY_EMPTY, 1);
    }
}
