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
 * Controls Climber in the state interrupt system
 */

public class Climber_States implements Behavior {

	private static final Logger logger = LogManager.getLogger(Climber_States.class);
	private static final Set<String> subsystems = Set.of("ss_climber");

	private final InputValues sharedInputValues;
	private final OutputValues sharedOutputValues;
	private final String joystick;
	private final String overrideButton;
	private final double minimumClimberPosition;
	private final double maximumClimberPosition;
	private final double overrideScaleFactor;

	private double winchSpeed;
	private boolean useJoystick;
	private boolean ratchetEngaged;


	public Climber_States(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
		sharedInputValues = inputValues;
		sharedOutputValues = outputValues;
		joystick = robotConfiguration.getString("global_climber", "joystick");
		overrideButton = robotConfiguration.getString("global_climber", "override_button");

		minimumClimberPosition = robotConfiguration.getDouble("global_climber", "minimum_climber_position");
		maximumClimberPosition = robotConfiguration.getDouble("global_climber", "maximum_climber_position");
		overrideScaleFactor = robotConfiguration.getDouble("global_climber", "override_scale_factor");
	}

	@Override
	public void initialize(String stateName, Config config) {
		logger.debug("Entering state {}", stateName);

		ratchetEngaged = config.getBoolean("ratchet_engaged", true);
		winchSpeed = config.getDouble("winch_speed", 0.0);
		useJoystick = config.getBoolean("use_joystick", true);
	}

	@Override
	public void update() {

		if (!sharedInputValues.getBoolean("ipb_endgame_enabled")) {
			return;
		}

		// Check variable to toggle joystick or state config, so it is not overridden by the controller.
		// For sequences and states that use a value other than the controller.
		if (useJoystick) {
			winchSpeed = sharedInputValues.getNumeric(joystick);
		}

		//Override button for encoder limits
		if (sharedInputValues.getBoolean(overrideButton)) {
			winchSpeed *= overrideScaleFactor;
		} else {
			//If climber runs too low, cut power and only allow to move in opposite direction
			if (winchSpeed < 0.0 && ((sharedInputValues.getNumeric("ipn_climber_winch_position")) < minimumClimberPosition)) {
				winchSpeed = 0.0;
			}

			if (winchSpeed > 0.0 && ((sharedInputValues.getNumeric("ipn_climber_winch_position")) > maximumClimberPosition)) {
				winchSpeed = 0.0;
			}
		}

		//Set winch to joystick value
		sharedOutputValues.setNumeric("opn_climber_winch", "percent", winchSpeed);

		// Set ratchet according to state
		sharedOutputValues.setBoolean("opb_climber_ratchet", ratchetEngaged);


	}

	@Override
	public void dispose() {
		sharedOutputValues.setNumeric("opn_climber_winch", "percent", 0.0);
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