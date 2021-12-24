package org.team1619.behavior;

import org.uacr.models.behavior.Behavior;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.shared.abstractions.OutputValues;
import org.uacr.shared.abstractions.RobotConfiguration;
import org.uacr.utilities.Config;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

import java.util.Set;


/**
 * Drives the robot in percent mode, based on the joystick values.
 */

public class Drivetrain_Percent implements Behavior {

    private static final Logger logger = LogManager.getLogger(Drivetrain_Percent.class);
    private static final Set<String> subsystems = Set.of("ss_drivetrain");

    private final InputValues sharedInputValues;
    private final OutputValues sharedOutputValues;
    private final String xAxis;
    private final String yAxis;

    private String stateName;

    public Drivetrain_Percent(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
        sharedInputValues = inputValues;
        sharedOutputValues = outputValues;
        xAxis = robotConfiguration.getString("global_drivetrain", "x");
        yAxis = robotConfiguration.getString("global_drivetrain", "y");

        stateName = "Unknown";
    }

    @Override
    public void initialize(String stateName, Config config) {
        logger.debug("Entering state {}", stateName);

        this.stateName = stateName;
    }

    @Override
    public void update() {
        double xAxis = sharedInputValues.getNumeric(this.xAxis);
        double yAxis = sharedInputValues.getNumeric(this.yAxis);

        // Set the motor speed to the joystick values
        double leftMotorSpeed = yAxis + xAxis;
        double rightMotorSpeed = yAxis - xAxis;

        //Because the joystick values combined can exceed the range the motors except (-1 to 1) limit them to with in that range
        if (leftMotorSpeed > 1) {
            rightMotorSpeed = rightMotorSpeed - (leftMotorSpeed - 1);
            leftMotorSpeed = 1;
        } else if (leftMotorSpeed < -1) {
            rightMotorSpeed = rightMotorSpeed - (1 + leftMotorSpeed);
            leftMotorSpeed = -1;
        } else if (rightMotorSpeed > 1) {
            leftMotorSpeed = leftMotorSpeed - (rightMotorSpeed - 1);
            rightMotorSpeed = 1;
        } else if (rightMotorSpeed < -1) {
            leftMotorSpeed = leftMotorSpeed - (1 + rightMotorSpeed);
            rightMotorSpeed = -1;
        }

        // Set the motors
        sharedOutputValues.setNumeric("opn_drivetrain_left", "percent", leftMotorSpeed);
        sharedOutputValues.setNumeric("opn_drivetrain_right", "percent", rightMotorSpeed);
    }


    @Override
    public void dispose() {
        logger.trace("Leaving state {}", stateName);
        sharedOutputValues.setNumeric("opn_drivetrain_left", "percent", 0.0);
        sharedOutputValues.setNumeric("opn_drivetrain_right", "percent", 0.0);
        sharedOutputValues.setBoolean("opb_drivetrain_gear_shifter", false);
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public Set<String> getSubsystems() {
        return subsystems;
    }
}