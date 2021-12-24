package org.uacr.utilities.services.managers;

import org.uacr.utilities.Lists;
import org.uacr.utilities.services.Service;
import org.uacr.utilities.services.ServiceState;
import org.uacr.utilities.services.ServiceWrapper;

import java.util.List;

/**
 * Runs a group of services sequentially
 */

public class LinearServiceManager extends ServiceManager {

    public LinearServiceManager(Service... services) {
        this(Lists.of(services));
    }

    public LinearServiceManager(List<Service> services) {
        super(services);

        setCurrentState(ServiceState.AWAITING_START);
    }

    @Override
    protected void onError(ServiceWrapper service, Exception exception) {
        RuntimeException e = new RuntimeException(service.getServiceName() + " has failed in a " + service.getServiceState() + " state", exception);

        e.setStackTrace(new StackTraceElement[]{});

        e.printStackTrace();

        if (getCurrentState() == ServiceState.STARTING) {
            stop();
        }
    }

    @Override
    public void start() {
        getExecutor().submit(() -> {
            startUp();
        });
    }

    // Starts the services
    protected void startUp() {
        setCurrentState(ServiceState.STARTING);

        for (ServiceWrapper service : getServices()) {
            startUpService(service);
        }

        if (getCurrentState() != ServiceState.STOPPING) {
            setCurrentState(ServiceState.RUNNING);
        }

        getHealthyLatch().countDown();
    }

    public void update() {
        getExecutor().submit(() -> {
            runUpdate();
        });
    }

    // Updates the services
    protected void runUpdate() {
        for (ServiceWrapper service : getServices()) {
            updateService(service);
        }
    }


    //Shuts down the services
    protected void shutDown() {
        setCurrentState(ServiceState.STOPPING);

        for (ServiceWrapper service : getServices()) {
            shutDownService(service);
        }

        getShutDownLatch().countDown();

        setCurrentState(ServiceState.STOPPED);
    }
}
