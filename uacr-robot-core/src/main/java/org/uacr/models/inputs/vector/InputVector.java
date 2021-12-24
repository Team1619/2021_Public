package org.uacr.models.inputs.vector;

import org.uacr.utilities.Config;

import java.util.Map;

/**
 * Base class for all vectors in the InputService
 **/

public abstract class InputVector {

    protected final Object name;

    /**
     * @param name the name of the InputVector.
     * @param config the configuration for the InputVector.
     */
    public InputVector(Object name, Config config) {
        this.name = name;
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
     * Used by the InputService to get the InputVector's current value
     * @return a map which is current values from the input.
     */
    public abstract Map<String, Double> get();

    /**
     * Called by the InputService to tell the InputVector to handle a flag.
     * Flags allow other parts of the code such as Behaviors to update the InputVectors settings.
     * @param flag the string for the InputVector to handle.
     */
    public abstract void processFlag(String flag);

    /**
     * @return the name of the InputVector
     */
    public Object getName() {
        return name;
    }
}
