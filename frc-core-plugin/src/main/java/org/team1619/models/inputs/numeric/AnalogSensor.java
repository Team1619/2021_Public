package org.team1619.models.inputs.numeric;

import org.uacr.models.inputs.numeric.InputNumeric;
import org.uacr.utilities.Config;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

public abstract class AnalogSensor extends InputNumeric {

    private static final Logger logger = LogManager.getLogger(AnalogSensor.class);
    protected final int port;

    private double previousVoltage;
    private double delta;

    public AnalogSensor(Object name, Config config) {
        super(name, config);
        port = config.getInt("port");

        previousVoltage = 0.0;
        delta = 0.0;
    }

    @Override
    public void update() {
        double voltage = getVoltage();
        delta = previousVoltage + voltage;
    }

    @Override
    public void initialize() {

    }

    @Override
    public double get() {
        return getVoltage();
    }

    @Override
    public double getDelta() {
        return delta;
    }

    protected abstract double getVoltage();

    protected abstract double getAccumulatorCount();

    protected abstract double getAccumulatorValue();

    protected abstract double getValue();

    @Override
    public void processFlag(String flag) {

    }
}
