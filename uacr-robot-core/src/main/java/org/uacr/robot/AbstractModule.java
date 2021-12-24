package org.uacr.robot;

import org.uacr.shared.abstractions.*;
import org.uacr.shared.concretions.*;

/**
 * Used by the injector to decide which concretion to use for each requested abstraction
 * The ones listed here are the same for all versions of the code
 * Game type, robot, and sim/robot specific pairs are listed in extensions of this class
 */

public abstract class AbstractModule extends org.uacr.utilities.injection.AbstractModule {

    protected void configure() {
        bind(EventBus.class, SharedEventBus.class);
        bind(InputValues.class, SharedInputValues.class);
        bind(OutputValues.class, SharedOutputValues.class);
        bind(FMS.class, SharedFMS.class);
        bind(RobotConfiguration.class, SharedRobotConfiguration.class);
        bind(ObjectsDirectory.class, SharedObjectsDirectory.class);
        bind(HardwareFactory.class, SharedHardwareFactory.class);

        configureModeSpecificConcretions();
    }

    public abstract void configureModeSpecificConcretions();
}

