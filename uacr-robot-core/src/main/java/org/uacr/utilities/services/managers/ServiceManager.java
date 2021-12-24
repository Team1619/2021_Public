package org.uacr.utilities.services.managers;

import org.uacr.utilities.services.Service;
import org.uacr.utilities.services.ServiceState;
import org.uacr.utilities.services.ServiceWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manages a group of services
 */

public abstract class ServiceManager {

    private final ExecutorService executor;
    private final List<ServiceWrapper> services;
    private final CountDownLatch healthyLatch;
    private final CountDownLatch shutDownLatch;

    private ServiceState currentState;

    //Create a new thread and serviceWrappers for each service that it is managing
    public ServiceManager(List<Service> services) {
        executor = Executors.newCachedThreadPool();
        this.services = Collections.synchronizedList(new ArrayList<>());

        healthyLatch = new CountDownLatch(1);
        shutDownLatch = new CountDownLatch(1);

        currentState = ServiceState.AWAITING_START;

        for (Service service : services) {
            this.services.add(new ServiceWrapper(service));
        }
    }

    // Gets the state of the service manager (same as the serviceStates)
    protected ServiceState getCurrentState() {
        synchronized (currentState) {
            return currentState;
        }
    }

    // Sets the state of the service manager (same as the serviceStates)
    protected void setCurrentState(ServiceState currentState) {
        synchronized (currentState) {
            this.currentState = currentState;
        }
    }

    // Gets a list of the services the serviceManager is managing
    protected List<ServiceWrapper> getServices() {
        synchronized (services) {
            return services;
        }
    }

    // Gets the Executor that is running the threadpool
    protected ExecutorService getExecutor() {
        synchronized (executor) {
            return executor;
        }
    }

    protected CountDownLatch getHealthyLatch() {
        return healthyLatch;
    }

    protected CountDownLatch getShutDownLatch() {
        return shutDownLatch;
    }

    // Starts one service
    protected void startUpService(ServiceWrapper service) {
        try {
            service.startUp();
        } catch (Exception e) {
            onError(service, e);
        }
    }

    // Updates one service
    protected void updateService(ServiceWrapper service) {
        try {
            service.runOneIteration();
        } catch (Exception e) {
            onError(service, e);
        }
    }

    // Shuts done one service
    protected void shutDownService(ServiceWrapper service) {
        try {
            service.shutDown();
        } catch (Exception e) {
            onError(service, e);
        }
    }

    // Executes when there is an error
    protected abstract void onError(ServiceWrapper service, Exception exception);

    // Tells the executor to use an open thread to call start up
    public abstract void start();

    // Waits until every service is running
    public void awaitHealthy() {
        try {
            getHealthyLatch().await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Tells the executor to use an open thread to call runUpdate
    public abstract void update();

    // Sets the state to stopping
    public void stop() {
        setCurrentState(ServiceState.STOPPING);
    }

    // Waits until the services are stopped
    public void awaitStopped() {
        try {
            getShutDownLatch().await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
