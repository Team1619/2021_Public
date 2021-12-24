package org.team1619.models.outputs.bool;

import org.uacr.models.outputs.bool.OutputBoolean;
import org.uacr.utilities.Config;

public abstract class SolenoidDouble extends OutputBoolean {

    protected final int deviceNumberPrimary;
    protected final int deviceNumberFollower;

    public SolenoidDouble(Object name, Config config) {
        super(name, config);

        deviceNumberPrimary = config.getInt("device_number_primary");
        deviceNumberFollower = config.getInt("device_number_follower");
    }
}
