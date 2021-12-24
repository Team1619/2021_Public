package org.uacr.utilities.services.managers;

import org.uacr.utilities.Lists;
import org.uacr.utilities.services.Service;
import org.uacr.utilities.services.ServiceState;
import org.uacr.utilities.services.ServiceWrapper;

import java.util.List;

/**
 * Runs the services as fast as possible skipping over ones that have not completed their frames
 */

public class SynchronizedServiceManager extends NonlinearServiceManager {

    public SynchronizedServiceManager(Service... services) {
        this(Lists.of(services));
    }

    public SynchronizedServiceManager(List<Service> services) {
        super(services);
    }

    @Override
    protected void requestServiceUpdate(ServiceWrapper service) {
        updateService(service);
    }
}
