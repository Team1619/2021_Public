package org.uacr.utilities.services.managers;

import org.uacr.utilities.Lists;
import org.uacr.utilities.services.Scheduler;
import org.uacr.utilities.services.Service;
import org.uacr.utilities.services.ServiceState;

import java.util.List;

/**
 * Runs the services sequentially using a scheduler
 */

public class ScheduledLinearServiceManager extends LinearServiceManager {

    private final Scheduler scheduler;

    public ScheduledLinearServiceManager(Scheduler scheduler, Service... services) {
        this(scheduler, Lists.of(services));
    }

    public ScheduledLinearServiceManager(Scheduler scheduler, List<Service> services) {
        super(services);

        this.scheduler = scheduler;
    }

    // States the services
    @Override
    public void start() {
        getExecutor().submit(() -> {
            scheduler.start();
            super.startUp();

            while (getCurrentState() == ServiceState.RUNNING) {
                try {
                    Thread.sleep(scheduler.nanosUntilNextRun());
                } catch (InterruptedException e) {
                }
                scheduler.run();
                super.runUpdate();
            }

            super.shutDown();
        });
    }
}
