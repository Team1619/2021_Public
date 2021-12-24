package org.team1619.models.inputs.vector;

import org.uacr.models.inputs.vector.InputVector;
import org.uacr.utilities.Config;

import java.util.HashMap;
import java.util.Map;

public abstract class Accelerometer extends InputVector {

    private Map<String, Double> acceleration = new HashMap<>();

    public Accelerometer(Object name, Config config) {
        super(name, config);
    }

    @Override
    public void update() {
        acceleration = getAcceleration();
    }

    @Override
    public void initialize() {

    }

    @Override
    public Map<String, Double> get() {
        return acceleration;
    }

    @Override
    public void processFlag(String flag) {

    }

    public abstract Map<String, Double> getAcceleration();
}
