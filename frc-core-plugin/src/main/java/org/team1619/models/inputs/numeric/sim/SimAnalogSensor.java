package org.team1619.models.inputs.numeric.sim;

import org.team1619.models.inputs.numeric.AnalogSensor;
import org.uacr.shared.abstractions.EventBus;
import org.uacr.utilities.Config;

public class SimAnalogSensor extends AnalogSensor {

    private final SimInputNumericListener listener;

    public SimAnalogSensor(EventBus eventBus, Object name, Config config) {
        super(name, config);

        listener = new SimInputNumericListener(eventBus, name);
    }

    @Override
    public double getVoltage() {
        return listener.get();
    }

    public double getAccumulatorCount() {
        return listener.get();
    }

    public double getAccumulatorValue() {
        return listener.get();
    }

    public double getValue() {
        return listener.get();
    }
}
