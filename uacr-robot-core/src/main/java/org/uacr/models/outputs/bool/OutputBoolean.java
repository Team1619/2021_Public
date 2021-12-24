package org.uacr.models.outputs.bool;

import org.uacr.utilities.Config;

/**
 * Base class for all booleans in the OutputService
 */

public abstract class OutputBoolean {

    protected final Object name;
    protected final boolean isInverted;

    /**
     * @param name the name of the OutputBoolean.
     * @param config the configuration for the OutputBoolean.
     */
    public OutputBoolean(Object name, Config config) {
        this.name = name;
        isInverted = config.getBoolean("inverted", false);
    }

    /**
     * Initializes the OutputBoolean class
     */
    public void initialize() {

    }

    /**
     * Sets the hardware object to the output value
     * @param outputValue the value to set to the hardware
     */
    public abstract void setHardware(boolean outputValue);

    /**
     * Called by the OutputService to tell the OutputBoolean to handle a flag.
     * Flags allow other parts of the code such as Behaviors to update the OutputBoolean settings.
     * @param flag the string for the OutputBoolean to handle.
     */
    public abstract void processFlag(String flag);
}
