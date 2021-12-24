package org.team1619.models.outputs.numeric.sim;

import org.team1619.models.outputs.numeric.Victor;
import org.uacr.shared.abstractions.HardwareFactory;
import org.uacr.shared.abstractions.ObjectsDirectory;
import org.uacr.utilities.Config;

import javax.annotation.Nullable;

/**
 * SimVictor extends Victor, and acts like victors in sim mode
 */

public class SimVictor extends Victor {

    private final Integer motor;

    private double output = 0.0;

    public SimVictor(Object name, Config config, HardwareFactory hardwareFactory) {
        super(name, config);

        // Included to mimic RobotTalon for testing
        motor = hardwareFactory.get(Integer.class, deviceNumber);
    }

    @Override
    public void processFlag(String flag) {

    }

    @Override
    public void setHardware(String outputType, double outputValue, String profile) {
        output = outputValue;
    }
}
