package org.uacr.shared.abstractions;

/**
 * Handles the distribution of information when running in Sim
 */

public interface EventBus {

    void register(Object object);

    void post(Object object);

    void unregister(Object object);
}
