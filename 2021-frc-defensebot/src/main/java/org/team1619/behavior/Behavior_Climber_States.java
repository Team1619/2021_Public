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
 * climber behavior
 */

public class Behavior_Climber_States implements Behavior {

	private static final Logger logger = LogManager.getLogger(Behavior_Climber_States.class);
	private static final Set<String> subsystems = Set.of("ss_climber");

	private final InputValues sharedInputValues;
	private final OutputValues sharedOutputValues;
	private final String climberJoystick;
	private double servoPosition;
	private double winchSpeed;
	private boolean useJoystick;


	public Behavior_Climber_States(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
		sharedInputValues = inputValues;
		sharedOutputValues = outputValues;
		climberJoystick = robotConfiguration.getString("global_climber", "climber_joystick");
	}

	@Override
	public void initialize(String stateName, Config config) {
		logger.debug("Entering state {}", stateName);

		servoPosition = config.getDouble("servo_zero_offset", 0.0);
		winchSpeed = config.getDouble("winch_speed", 0.0);
		useJoystick = config.getBoolean("use_joystick", true);
	}

	@Override
	public void update() {

		if (!sharedInputValues.getBoolean("ipb_endgame_enabled")){
			return;
		}

		// Check variable to toggle joystick or state config, so it is not overridden by the controller.
		// For sequences and states that use a value other than the controller.
		if (useJoystick) {
			winchSpeed = sharedInputValues.getNumeric(climberJoystick);
		}

		sharedOutputValues.setNumeric("opn_climber_winch", "percent", winchSpeed);

		sharedOutputValues.setNumeric("opn_climber_ratchet", "", servoPosition);

	}

	@Override
	public void dispose() {

		sharedOutputValues.setNumeric("opn_climber_winch", "percent", 0.0);

		sharedOutputValues.setNumeric("opn_climber_ratchet", "", 0.7);
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