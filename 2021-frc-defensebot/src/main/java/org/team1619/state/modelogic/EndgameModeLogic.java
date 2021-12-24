package org.team1619.state.modelogic;

import org.uacr.models.state.State;
import org.uacr.robot.AbstractModeLogic;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.shared.abstractions.RobotConfiguration;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

/**
 * Handles the isReady and isDone logic for endgame mode on competition bot
 */

public class EndgameModeLogic extends AbstractModeLogic {

	private static final Logger logger = LogManager.getLogger(EndgameModeLogic.class);
	private final String climberJoystick;

	public EndgameModeLogic(InputValues inputValues, RobotConfiguration robotConfiguration) {
		super(inputValues, robotConfiguration);
		climberJoystick = robotConfiguration.getString("global_climber", "climber_joystick");
	}

	@Override
	public void initialize() {
		logger.info("***** ENDGAME *****");
	}

	@Override
	public void update() {

	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean isReady(String name) {
		switch (name) {
			case "sq_climber_extend":
				return sharedInputValues.getNumeric(climberJoystick) > 0;
			case "st_climber_retract":
				return sharedInputValues.getNumeric(climberJoystick) < 0;
			default:
				return false;
		}
	}

	@Override
	public boolean isDone(String name, State state) {
		switch (name) {
			case "sq_climber_extend":
				return !(sharedInputValues.getNumeric("ipn_operator_left_y") > 0);
			case "st_climber_retract":
				return !(sharedInputValues.getNumeric("ipn_operator_left_y") < 0);
			default:
				return state.isDone();
		}
	}
}
