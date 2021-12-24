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
 * Drives the robot in percent mode or velocity mode depending on the mode button, based on the joystick values.
 */

public class Drivetrain implements Behavior {

	private static final Logger logger = LogManager.getLogger(Drivetrain.class);
	private static final Set<String> subsystems = Set.of("ss_drivetrain");

	private final InputValues sharedInputValues;
	private final OutputValues sharedOutputValues;
	private final String xAxis;
	private final String yAxis;
	private final String gearShiftButton;
	private final String velocityModeButton;

	private boolean velocityMode;

	private String stateName;

	public Drivetrain(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
		sharedInputValues = inputValues;
		sharedOutputValues = outputValues;
		xAxis = robotConfiguration.getString("global_drivetrain", "x");
		yAxis = robotConfiguration.getString("global_drivetrain", "y");
		gearShiftButton = robotConfiguration.getString("global_drivetrain", "gear_shift_button");
		velocityModeButton = robotConfiguration.getString("global_drivetrain", "velocity_mode_button");

		stateName = "Unknown";
	}

	@Override
	public void initialize(String stateName, Config config) {
		logger.debug("Entering state {}", stateName);

		velocityMode = config.getBoolean("velocity_mode");

		this.stateName = stateName;
	}

	@Override
	public void update() {
		double xAxis = sharedInputValues.getNumeric(this.xAxis);
		double yAxis = sharedInputValues.getNumeric(this.yAxis);
		boolean gearShiftButtonValue = sharedInputValues.getBoolean(gearShiftButton);

		// Set the motor speed to the joystick values
		double leftMotorSpeed = yAxis + xAxis;
		double rightMotorSpeed = yAxis - xAxis;

		// If the numbers are greater than 1 then get the max and calculate the percentages below
		double maxAbs = maxAbs(leftMotorSpeed, rightMotorSpeed, 1.0);

		// Divide the numbers if one of the motor speeds is over 1. If not dividing by one will be the same number.
		leftMotorSpeed = leftMotorSpeed/maxAbs;
		rightMotorSpeed = rightMotorSpeed/maxAbs;

		if (velocityMode) {
			// Command the motors in velocity mode
			sharedOutputValues.setNumeric("opn_drivetrain_left", "velocity", leftMotorSpeed * 12, "pr_drive");
			sharedOutputValues.setNumeric("opn_drivetrain_right", "velocity", rightMotorSpeed * 12, "pr_drive");
		}
		else {
			// Command the motors in percent mode
			sharedOutputValues.setNumeric("opn_drivetrain_left", "percent", leftMotorSpeed);
			sharedOutputValues.setNumeric("opn_drivetrain_right", "percent", rightMotorSpeed);
		}
		//Set the gear shifters
		sharedOutputValues.setBoolean("opb_drivetrain_gear_shifter", gearShiftButtonValue);
		sharedInputValues.setBoolean("ipb_is_low_gear", gearShiftButtonValue);
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

	// Passing in 3 variables, left motor speed, right motor speed and 1.0, we return the biggest of the three.
	// This handles situations in which the joystick values combined are larger than 1, because whichever is the
	// larger one, the code in update() sets the motor speed to the respective side (left or right) divided by the
	// largest motor speed, which, if it's 1.0, just results in being itself.
	private static double maxAbs(double firstNum, double secondNum, double thirdNum) {
		firstNum = Math.abs(firstNum);
		secondNum = Math.abs(secondNum);
		thirdNum = Math.abs(thirdNum);

		if (firstNum >= secondNum && firstNum >= thirdNum) {
			return firstNum;
		} else if (secondNum >= firstNum && secondNum >= thirdNum) {
			return secondNum;
		} else if (thirdNum >= firstNum && thirdNum >= secondNum) {
			return thirdNum;
		} else {
			return 0.0;
		}
	}
}