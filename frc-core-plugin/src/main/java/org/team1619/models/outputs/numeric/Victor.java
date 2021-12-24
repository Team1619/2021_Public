package org.team1619.models.outputs.numeric;

import org.uacr.utilities.Config;

/**
 * Victor is a motor object, which is extended to control victors
 */

public abstract class Victor extends CTREMotor {

    public Victor(Object name, Config config) {
        super(name, config);
    }
}
