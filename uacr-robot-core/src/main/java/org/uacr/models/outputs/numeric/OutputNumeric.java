package org.uacr.models.outputs.numeric;

import org.uacr.utilities.Config;

/**
 * Base class for all numerics in the OutputService
 */

public abstract class OutputNumeric {

    protected final Object name;
    protected final boolean isInverted;

    /**
     * @param name the name of the OutputNumeric.
     * @param config the configuration for the OutputNumeric.
     */
    public OutputNumeric(Object name, Config config) {
        this.name = name;
        isInverted = config.getBoolean("inverted", false);
    }

    /**
     * Initializes the OutputNumeric class
     */
    public void initialize() {

    }

    /**
     * Sets the hardware object to the output value
     * @param outputValue the value to set to the hardware
     */
    public abstract void setHardware(String outputType, double outputValue, String profile);

    /**
     * Called by the OutputNumeric to tell the OutputNumeric to handle a flag.
     * Flags allow other parts of the code such as Behaviors to update the OutputNumeric settings.
     * @param flag the string for the OutputNumeric to handle.
     */
    public abstract void processFlag(String flag);
}


