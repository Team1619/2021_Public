package org.team1619.models.inputs.bool.sim;

import org.uacr.events.sim.SimInputBooleanSetEvent;
import org.uacr.shared.abstractions.EventBus;
import org.uacr.utilities.eventbus.Subscribe;

public class SimInputBooleanListener {

    private final Object name;

    private boolean value = false;

    public SimInputBooleanListener(EventBus eventBus, Object name) {
        this.name = name;
        eventBus.register(this);
    }

    public boolean get() {
        return value;
    }

    @Subscribe
    public void onInputBooleanSet(SimInputBooleanSetEvent event) {
        if (event.name.equals(name)) {
            value = event.value;
        }
    }
}
