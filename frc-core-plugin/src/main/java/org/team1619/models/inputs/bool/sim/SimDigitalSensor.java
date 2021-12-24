package org.team1619.models.inputs.bool.sim;

import org.team1619.models.inputs.bool.DigitalSensor;
import org.uacr.shared.abstractions.EventBus;
import org.uacr.utilities.Config;

public class SimDigitalSensor extends DigitalSensor {

    private final SimInputBooleanListener listener;

    public SimDigitalSensor(EventBus eventBus, Object name, Config config) {
        super(name, config);

        listener = new SimInputBooleanListener(eventBus, name);
    }

    @Override
    public boolean getDigitalInputValue() {
        return listener.get();
    }
}
