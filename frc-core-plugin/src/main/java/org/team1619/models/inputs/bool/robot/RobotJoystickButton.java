package org.team1619.models.inputs.bool.robot;

import edu.wpi.first.wpilibj.Joystick;
import org.team1619.models.inputs.bool.Button;
import org.uacr.shared.abstractions.HardwareFactory;
import org.uacr.utilities.Config;

public class RobotJoystickButton extends Button {

    private final Joystick joystick;

    public RobotJoystickButton(Object name, Config config, HardwareFactory hardwareFactory) {
        super(name, config);

        joystick = hardwareFactory.get(Joystick.class, port);
    }

    @Override
    public boolean isPressed() {
        return joystick.getRawButton(Integer.valueOf(button));
    }
}
