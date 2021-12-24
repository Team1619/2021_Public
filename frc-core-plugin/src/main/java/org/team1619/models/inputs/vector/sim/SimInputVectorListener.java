package org.team1619.models.inputs.vector.sim;

import org.uacr.events.sim.SimInputVectorSetEvent;
import org.uacr.shared.abstractions.EventBus;
import org.uacr.utilities.eventbus.Subscribe;

import java.util.Map;

public class SimInputVectorListener {

    private final Object name;

    private Map<String, Double> values;

    public SimInputVectorListener(EventBus eventBus, Object name, Map<String, Double> startingValues) {
        eventBus.register(this);
        this.name = name;
        values = startingValues;
    }

    public Map<String, Double> get() {
        return values;
    }

    @Subscribe
    public void onInputVectorSet(SimInputVectorSetEvent event) {
        if (event.name.equals(name)) {
            values = event.values;
        }
    }
}
