package org.uacr.events.sim;

/**
 * Eventbus event for setting an InputNumeric in sim mode
 */

public class SimInputNumericSetEvent {

    public final double value;
    public final String name;

    /**
     * @param name the name of the InputNumeric
     * @param value the new value for the InputNumeric
     */
    public SimInputNumericSetEvent(String name, double value) {
        this.value = value;
        this.name = name;
    }
}
