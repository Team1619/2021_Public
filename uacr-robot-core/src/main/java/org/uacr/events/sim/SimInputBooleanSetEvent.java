package org.uacr.events.sim;

/**
 * Eventbus event for setting an InputBoolean in sim mode
 */

public class SimInputBooleanSetEvent {

    public final boolean value;
    public final String name;

    /**
     * @param name the name of the InputBoolean
     * @param value the new value for the InputBoolean
     */
    public SimInputBooleanSetEvent(String name, boolean value) {
        this.value = value;
        this.name = name;
    }
}
