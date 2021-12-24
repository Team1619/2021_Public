package org.team1619.models.inputs.numeric.robot;

import edu.wpi.first.wpilibj.XboxController;
import org.team1619.models.inputs.numeric.Axis;
import org.uacr.shared.abstractions.HardwareFactory;
import org.uacr.utilities.Config;

public class RobotControllerAxis extends Axis {

    private final XboxController controller;

    public RobotControllerAxis(Object name, Config config, HardwareFactory hardwareFactory) {
        super(name, config);

        controller = hardwareFactory.get(XboxController.class, port);
    }

    @Override
    public double getAxis() {
        return controller.getRawAxis(axis);
    }
}
