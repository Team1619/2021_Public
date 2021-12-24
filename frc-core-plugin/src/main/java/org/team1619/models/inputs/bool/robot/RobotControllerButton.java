package org.team1619.models.inputs.bool.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import org.team1619.models.inputs.bool.Button;
import org.uacr.shared.abstractions.HardwareFactory;
import org.uacr.utilities.Config;

public class RobotControllerButton extends Button {

    private final XboxController controller;

    public RobotControllerButton(Object name, Config config, HardwareFactory hardwareFactory) {
        super(name, config);

        controller = hardwareFactory.get(XboxController.class, port);
    }

    @Override
    public boolean isPressed() {
        switch (button) {
            case "a":
                return controller.getAButton();
            case "x":
                return controller.getXButton();
            case "y":
                return controller.getYButton();
            case "b":
                return controller.getBButton();
            case "start":
                return controller.getStartButton();
            case "back":
                return controller.getBackButton();
            case "left_bumper":
                return controller.getBumper(GenericHID.Hand.kLeft);
            case "right_bumper":
                return controller.getBumper(GenericHID.Hand.kRight);
            case "left_stick_button":
                return controller.getStickButton(GenericHID.Hand.kLeft);
            case "right_stick_button":
                return controller.getStickButton(GenericHID.Hand.kRight);
            case "d_pad_up":
                //getPOV(0) returns 0 when not plugged in
                if (controller.getPOVCount() != 0) {
                    return controller.getPOV(0) == 0;
                }
                return false;
            case "d_pad_down":
                return controller.getPOV(0) == 180;

            case "d_pad_left":
                return controller.getPOV(0) == 270;

            case "d_pad_right":
                return controller.getPOV(0) == 90;
            case "right_trigger":
                return controller.getRawAxis(3) > 0.5;
            case "left_trigger":
                return controller.getRawAxis(2) > 0.5;
            default:
                return false;
        }
    }
}
