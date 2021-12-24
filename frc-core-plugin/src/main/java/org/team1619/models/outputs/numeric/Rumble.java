package org.team1619.models.outputs.numeric;

import org.uacr.models.outputs.numeric.OutputNumeric;
import org.uacr.utilities.Config;

/**
 * Rumble is a motor object, which is extended to control the xbox controller rumble motors
 */

public abstract class Rumble extends OutputNumeric {

    protected final Object name;
    protected final int port;
    protected final String rumbleSide;

    public Rumble(Object name, Config config) {
        super(name, config);

        this.name = name;
        port = config.getInt("port");
        rumbleSide = config.getString("rumble_side", "none");
    }
}
