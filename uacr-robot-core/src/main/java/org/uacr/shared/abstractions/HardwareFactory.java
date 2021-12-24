package org.uacr.shared.abstractions;

/**
 * Models instantiating and storing external hardware objects
 */

public interface HardwareFactory {

    /**
     * Finds, instantiates, stores, and returns external hardware objects for framework hardware objects
     *
     * @param tClass the class of the external hardware object to be returned
     * @param parameters the constructor parameters of the external hardware object to be returned
     * @param <T> the type of the object to be returned
     * @return an instance of the requested class
     */
    <T> T get(Class<T> tClass, Object... parameters);
}
