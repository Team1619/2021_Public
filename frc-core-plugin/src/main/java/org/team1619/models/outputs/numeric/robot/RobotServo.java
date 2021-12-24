package org.team1619.models.outputs.numeric.robot;

import edu.wpi.first.wpilibj.Servo;
import org.uacr.shared.abstractions.HardwareFactory;
import org.uacr.utilities.Config;

/**
 * RobotServo extends Servo, and controls servo motors on the robot
 */

public class RobotServo extends org.team1619.models.outputs.numeric.Servo {

    private final Servo servo;

    public RobotServo(Object name, Config config, HardwareFactory hardwareFactory) {
        super(name, config);
        servo = hardwareFactory.get(Servo.class, channel);
    }

    @Override
    public void processFlag(String flag) {

    }

    @Override
    public void setHardware(String outputType, double outputValue, String profile) {
        if (outputValue < 0) {
            servo.setDisabled();
        } else {
            servo.set(outputValue);
        }
    }

    public double getSetpoint() {
        return servo.getPosition();
    }
}
