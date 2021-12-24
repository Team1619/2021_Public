package org.team1619.models.outputs.numeric;

import org.uacr.models.outputs.numeric.OutputNumeric;
import org.uacr.utilities.Config;

/**
 * CTREMotor is a class that stores data specific to CTRE motors, TalonSRX and VictorSPX
 */

public abstract class CTREMotor extends OutputNumeric {

    protected final boolean isBrakeModeEnabled;
    protected final int deviceNumber;

    public CTREMotor(Object name, Config config) {
        super(name, config);

        deviceNumber = config.getInt("device_number");
        isBrakeModeEnabled = config.getBoolean("brake_mode_enabled", true);
    }

    public int getDeviceNumber() {
        return deviceNumber;
    }
}
