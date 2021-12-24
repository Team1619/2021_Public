package org.team1619.models.outputs.bool;

import org.uacr.models.outputs.bool.OutputBoolean;
import org.uacr.utilities.Config;

public abstract class SolenoidSingle extends OutputBoolean {

    protected final int deviceNumber;

    public SolenoidSingle(Object name, Config config) {
        super(name, config);

        deviceNumber = config.getInt("device_number");
    }
}
