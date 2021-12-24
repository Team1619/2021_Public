package org.team1619.models.inputs.numeric;

import org.uacr.models.inputs.numeric.InputNumeric;
import org.uacr.utilities.Config;

public abstract class Axis extends InputNumeric {

    protected final int port;
    protected final int axis;

    private double axisValue = 0.0;
    private double delta = 0.0;
    private double deadBand = 0.0;
    private double scale = 0.0;

    public Axis(Object name, Config config) {
        super(name, config);

        port = config.getInt("port");
        axis = config.getInt("axis");

        deadBand = config.getDouble("deadband", 0.0);
        scale = config.getDouble("scale", 1.0);
    }

    @Override
    public void update() {
        double nextAxis = isInverted ? -getAxis() : getAxis();
        if (Math.abs(nextAxis) < deadBand) {
            nextAxis = 0.0;
        }
        nextAxis = nextAxis * scale;
        delta = nextAxis - axisValue;
        axisValue = nextAxis;
    }

    @Override
    public void initialize() {

    }

    @Override
    public double get() {
        return axisValue;
    }

    @Override
    public double getDelta() {
        return delta;
    }

    protected abstract double getAxis();

    @Override
    public void processFlag(String flag) {

    }
}
