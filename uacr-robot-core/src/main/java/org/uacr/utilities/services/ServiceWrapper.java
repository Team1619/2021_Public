package org.uacr.utilities.services;

import javax.annotation.Nullable;

/**
 * Manages one service with or without a scheduler
 */

public class ServiceWrapper implements Service {

    private final Service service;

    @Nullable
    private final Scheduler scheduler;
    @Nullable
    private ServiceState serviceState;
    private boolean isCurrentlyRunning;

    public ServiceWrapper(Service service) {
        this.service = service;

        // Gets a scheduler if it is a scheduled service
        if (service instanceof ScheduledService) {
            scheduler = ((ScheduledService) service).scheduler();
        } else {
            scheduler = null;
        }

        serviceState = ServiceState.AWAITING_START;
        isCurrentlyRunning = false;
    }

    // Returns the current state of the service
    public ServiceState getServiceState() {
        return serviceState;
    }

    /**
     * Determines whether a service should run based on the service's current state and a scheduler if included in the service
     */
    public boolean shouldRun() {
        if (serviceState == ServiceState.AWAITING_START || serviceState == ServiceState.STOPPING) {
            return false;
        }

        // Do nothing if runOneIteration in the service is currently running
        if (isCurrentlyRunning()) {
            return false;
        }

        if (scheduler != null) {
            return scheduler.shouldRun();
        }

        return true;
    }

    public long nanosUntilNextRun() {
        if (scheduler != null) {
            return scheduler.nanosUntilNextRun();
        }

        return 0;
    }

    /**
     * @return the time until the next cycle should start
     */
    public long nextRunTimeNanos() {
        if (isCurrentlyRunning()) {
            return Long.MAX_VALUE;
        }


        if (scheduler != null) {
            return scheduler.nextRunTimeNanos();
        }

        return 0;
    }

    /**
     * @return true if a the service is running
     */
    public boolean isCurrentlyRunning() {
        return isCurrentlyRunning;
    }

    /**
     * Allows for the service managers to set whether this service is currently running
     *
     * @param currentlyRunning whether the service is currently running
     */
    public void setCurrentlyRunning(boolean currentlyRunning) {
        isCurrentlyRunning = currentlyRunning;
    }

    /**
     * @return the name of the order
     */
    public String getServiceName() {
        return service.getClass().getSimpleName();
    }

    /**
     * Starts the scheduler and starts the service
     */
    @Override
    public synchronized void startUp() throws Exception {
        Thread.currentThread().setName(getServiceName());

        serviceState = ServiceState.STARTING;

        if (scheduler != null) {
            scheduler.start();
        }

        service.startUp();
    }

    /**
     * Calls runOneIteration on the service and waits for it to complete before moving on
     */
    @Override
    public synchronized void runOneIteration() throws Exception {
        Thread.currentThread().setName(getServiceName());

        isCurrentlyRunning = true;

        if (scheduler != null) {
            scheduler.run();
        }

        serviceState = ServiceState.RUNNING;

        try {
            service.runOneIteration();
        } finally {
            isCurrentlyRunning = false;
        }
    }

    /**
     * Shuts down the service
     */
    @Override
    public synchronized void shutDown() throws Exception {
        Thread.currentThread().setName(getServiceName());

        serviceState = ServiceState.STOPPING;

        service.shutDown();

        serviceState = ServiceState.STOPPED;
    }
}
