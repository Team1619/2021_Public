package org.uacr.models.inputs.bool;

import org.uacr.utilities.Config;

/**
 * Base class for all booleans in the InputService
 **/

public abstract class InputBoolean {

    protected final Object name;
    protected final boolean isInverted;

    /**
     * @param name the name of the InputBoolean.
     * @param config the configuration for the InputBoolean.
     */
    public InputBoolean(Object name, Config config) {
        this.name = name;
        isInverted = config.getBoolean("inverted", false);
    }

    /**
     * Called by the InputService at start up.
     * Use to setup the input for the running.
     */
    public abstract void initialize();

    /**
     * Called every frame by the InputService.
     * Use to update the input hardware.
     */
    public abstract void update();

    /**
     * Used by the InputService to get the InputBooleans current value
     * @return a boolean which is the current value from the input.
     */
    public abstract boolean get();

    /**
     * 
     * @return the InputBoolean's current delta
     */
    public abstract DeltaType getDelta();

    /**
     * Called by the InputService to tell the InputBoolean to handle a flag.
     * Flags allow other parts of the code such as Behaviors to update the InputBoolean's settings.
     * @param flag the string for the InputBoolean to handle.
     */
    public abstract void processFlag(String flag);

    /**
     * The types of deltas for an InputBoolean
     */
    public enum DeltaType {
        RISING_EDGE,
        FALLING_EDGE,
        NO_DELTA
    }
}
