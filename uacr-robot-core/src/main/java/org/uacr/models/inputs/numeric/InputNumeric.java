package org.uacr.models.inputs.numeric;

import org.uacr.utilities.Config;

/**
 * Base class for all numerics in the InputService
 **/

public abstract class InputNumeric {

    protected final Object name;
    protected final boolean isInverted;

    /**
     * @param name the name of the InputNumeric.
     * @param config the configuration for the InputNumeric.
     */
    public InputNumeric(Object name, Config config) {
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
     * Used by the InputService to get the InputNumeric's current value
     * @return a double which is current value from the input.
     */
    public abstract double get();

    /**
     * @return the InputNumeric's current delta
     */
    public abstract double getDelta();

    /**
     * Called by the InputService to tell the InputNumeric to handle a flag.
     * Flags allow other parts of the code such as Behaviors to update the InputNumerics settings.
     * @param flag the string for the InputNumeric to handle.
     */
    public abstract void processFlag(String flag);
}


