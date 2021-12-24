package org.team1619.models.inputs.numeric.sim;

import org.uacr.events.sim.SimInputNumericSetEvent;
import org.uacr.shared.abstractions.EventBus;
import org.uacr.utilities.eventbus.Subscribe;

public class SimInputNumericListener {

    private final Object name;

    private double value = 0.0;

    public SimInputNumericListener(EventBus eventBus, Object name) {
        eventBus.register(this);
        this.name = name;
    }

    public double get() {
        return value;
    }

    @Subscribe
    public void onInputNumericSet(SimInputNumericSetEvent event) {
        if (event.name.equals(name)) {
            value = event.value;
        }
    }
}
