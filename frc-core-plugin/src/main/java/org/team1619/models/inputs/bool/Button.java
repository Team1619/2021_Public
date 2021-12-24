package org.team1619.models.inputs.bool;

import org.uacr.models.inputs.bool.InputBoolean;
import org.uacr.utilities.Config;

public abstract class Button extends InputBoolean {

    protected final int port;
    protected final String button;

    private DeltaType delta = DeltaType.NO_DELTA;
    private boolean isPressed = false;

    public Button(Object name, Config config) {
        super(name, config);

        port = config.getInt("port");
        button = config.getString("button");
    }

    @Override
    public void update() {
        boolean nextIsPressed = isInverted != isPressed();

        if (nextIsPressed && !isPressed) {
            delta = DeltaType.RISING_EDGE;
        } else if (!nextIsPressed && isPressed) {
            delta = DeltaType.FALLING_EDGE;
        } else {
            delta = DeltaType.NO_DELTA;
        }

        isPressed = nextIsPressed;
    }

    @Override
    public void initialize() {

    }

    @Override
    public boolean get() {
        return isPressed;
    }

    @Override
    public DeltaType getDelta() {
        return delta;
    }

    public abstract boolean isPressed();

    @Override
    public void processFlag(String flag) {

    }
}
