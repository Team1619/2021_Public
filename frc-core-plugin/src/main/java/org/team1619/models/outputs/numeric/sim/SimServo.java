package org.team1619.models.outputs.numeric.sim;

import org.uacr.utilities.Config;

/**
 * SimServo extends Servo, and acts like servo motors in sim mode
 */

public class SimServo extends org.team1619.models.outputs.numeric.Servo {

    private double output = 0.0;

    public SimServo(Object name, Config config) {
        super(name, config);
    }

    @Override
    public void processFlag(String flag) {

    }

    @Override
    public void setHardware(String outputType, double outputValue, String profile) {
        output = outputValue;
    }
}
