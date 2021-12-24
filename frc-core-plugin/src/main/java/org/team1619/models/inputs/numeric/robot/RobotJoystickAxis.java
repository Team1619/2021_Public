package org.team1619.models.inputs.numeric.robot;

import edu.wpi.first.wpilibj.Joystick;
import org.team1619.models.inputs.numeric.Axis;
import org.uacr.shared.abstractions.HardwareFactory;
import org.uacr.utilities.Config;

public class RobotJoystickAxis extends Axis {

    private final Joystick joystick;

    public RobotJoystickAxis(Object name, Config config, HardwareFactory hardwareFactory) {
        super(name, config);

        joystick = hardwareFactory.get(Joystick.class, port);
    }

    @Override
    public double getAxis() {
        return joystick.getRawAxis(axis);
    }
}
