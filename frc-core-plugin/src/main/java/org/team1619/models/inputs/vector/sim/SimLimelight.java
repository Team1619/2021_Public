package org.team1619.models.inputs.vector.sim;

import org.team1619.models.inputs.vector.Limelight;
import org.uacr.shared.abstractions.EventBus;
import org.uacr.utilities.Config;

import java.util.Map;

public class SimLimelight extends Limelight {

    private final SimInputVectorListener listener;

    public SimLimelight(EventBus eventBus, Object name, Config config) {
        super(name, config);

        listener = new SimInputVectorListener(eventBus, name, Map.of("tv", 0.0, "tx", 0.0, "ty", 0.0, "ta", 0.0, "ts", 0.0, "tl", 0.0));
    }

    @Override
    public Map<String, Double> getData() {
        return listener.get();
    }

    @Override
    public void initialize() {

    }
}
