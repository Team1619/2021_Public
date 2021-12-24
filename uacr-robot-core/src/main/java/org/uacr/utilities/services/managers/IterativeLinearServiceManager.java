package org.uacr.utilities.services.managers;

import org.uacr.utilities.Lists;
import org.uacr.utilities.services.Service;
import org.uacr.utilities.services.ServiceState;

import java.util.List;

/**
 * Runs the services sequentially as fast a possible
 */

public class IterativeLinearServiceManager extends LinearServiceManager {

    public IterativeLinearServiceManager(Service... services) {
        this(Lists.of(services));
    }

    public IterativeLinearServiceManager(List<Service> services) {
        super(services);
    }

    @Override
    public void start() {
        getExecutor().submit(() -> {
            super.startUp();

            while (getCurrentState() == ServiceState.RUNNING) {
                runUpdate();
            }

            super.shutDown();
        });
    }
}
