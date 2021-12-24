package org.team1619.state.modelogic;

import org.uacr.models.state.State;
import org.uacr.robot.AbstractModeLogic;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.shared.abstractions.RobotConfiguration;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

/**
 * Handles the isReady and isDone logic for teleop mode on competition bot
 */

public class TeleopModeLogic extends AbstractModeLogic {

	private static final Logger logger = LogManager.getLogger(TeleopModeLogic.class);

	public TeleopModeLogic(InputValues inputValues, RobotConfiguration robotConfiguration) {
		super(inputValues, robotConfiguration);
	}

	@Override
	public void initialize() {
		logger.info("***** TELEOP *****");
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
//			case "st_example_zero":
//				return !sharedInputValues.getBoolean("ipb_example_has_been_zeroed");
			case "st_example":
				return sharedInputValues.getBooleanRisingEdge("ipb_operator_right_trigger");
			default:
				return false;
		}
	}

	@Override
	public boolean isDone(String name, State state) {
		switch (name) {
			default:
				return state.isDone();
		}
	}
}
