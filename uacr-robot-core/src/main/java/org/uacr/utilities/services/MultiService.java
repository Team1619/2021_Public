package org.uacr.utilities.services;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Runs multiple services at one time
 */

public class MultiService implements Service {

    private final List<Service> services;

    public MultiService(List<Service> services) {
        this.services = services;
    }

    public MultiService(Service... services) {
        this(Arrays.asList(services));
    }

    /**
     * Starts up all services handled by this multiService in a single thread
     */
    @Override
    public void startUp() throws Exception {
        for (Service service : services) {
            Thread.currentThread().setName(service.getClass().getSimpleName());
            service.startUp();
        }
    }

    /**
     * Calls runOneIteration all services handled by this multiService in a single thread
     */
    @Override
    public void runOneIteration() throws Exception {
        for (Service service : services) {
            Thread.currentThread().setName(service.getClass().getSimpleName());
            service.runOneIteration();
        }
    }

    /**
     * Shuts down all services handled by this multiService in a single thread
     */
    @Override
    public void shutDown() throws Exception {
        for (Service service : services) {
            Thread.currentThread().setName(service.getClass().getSimpleName());
            service.shutDown();
        }
    }
}
