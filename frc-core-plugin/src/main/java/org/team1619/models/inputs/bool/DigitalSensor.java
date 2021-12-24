package org.team1619.models.inputs.bool;

import org.uacr.models.inputs.bool.InputBoolean;
import org.uacr.utilities.Config;

public abstract class DigitalSensor extends InputBoolean {

    protected final int id;

    private boolean previousValue;
    private boolean value;

    public DigitalSensor(Object name, Config config) {
        super(name, config);

        id = config.getInt("id");
    }

    public abstract boolean getDigitalInputValue();

    @Override
    public void initialize() {
        value = getDigitalInputValue();
    }

    @Override
    public void update() {
        previousValue = value;
        value = getDigitalInputValue();
    }

    @Override
    public boolean get() {
        return isInverted != value;
    }

    @Override
    public DeltaType getDelta() {
        if (!previousValue && value) {
            return DeltaType.RISING_EDGE;
        } else if (previousValue && !value) {
            return DeltaType.FALLING_EDGE;
        } else {
            return DeltaType.NO_DELTA;
        }
    }

    @Override
    public void processFlag(String flag) {

    }
}
