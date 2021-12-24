package org.team1619.models.outputs.numeric.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import org.team1619.models.outputs.numeric.Rumble;
import org.uacr.shared.abstractions.HardwareFactory;
import org.uacr.utilities.Config;

/**
 * RobotRumble extends Rumble, and controls xbox controller rumble motors when running on the robot
 */

public class RobotRumble extends Rumble {

    private final XboxController rumble;

    private double adjustedOutput;

    public RobotRumble(Object name, Config config, HardwareFactory hardwareFactory) {
        super(name, config);

        rumble = hardwareFactory.get(XboxController.class, port);

        adjustedOutput = 0.0;
    }

    @Override
    public void processFlag(String flag) {

    }

    @Override
    public void setHardware(String outputType, double outputValue, String profile) {
        adjustedOutput = outputValue;
        if (rumbleSide.equals("right")) {
            rumble.setRumble(GenericHID.RumbleType.kRightRumble, adjustedOutput);
        } else if (rumbleSide.equals("left")) {
            rumble.setRumble(GenericHID.RumbleType.kLeftRumble, adjustedOutput);
        }
    }
}
