package org.team1619.models.outputs.numeric;

import org.uacr.models.outputs.numeric.OutputNumeric;
import org.uacr.utilities.Config;

/**
 * Servo is a motor object, which is extended to control servo
 */

public abstract class Servo extends OutputNumeric {

    protected final int channel;

    public Servo(Object name, Config config) {
        super(name, config);

        channel = config.getInt("device_channel");
    }
}
