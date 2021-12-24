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

	public EndgameModeLogic(InputValues inputValues, RobotConfiguration robotConfiguration) {
		super(inputValues, robotConfiguration);
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
			case "st_collector_retract":
				return sharedInputValues.getBooleanRisingEdge("ipb_operator_left_bumper");
			case "st_collector_extend":
				return sharedInputValues.getBooleanRisingEdge("ipb_operator_left_trigger");
			case "sq_climber_extend":
				return (sharedInputValues.getNumeric("ipn_operator_left_y") > 0.0);
			case "st_climber_retract":
				return (sharedInputValues.getNumeric("ipn_operator_left_y") < 0.0);
			case "st_turret_endgame":
				return true;

			// ------- Undefined states -------
			default:
				return false;
		}
	}

	@Override
	public boolean isDone(String name, State state) {
		switch (name) {
			// Climber
			case "sq_climber_extend":
				return !(sharedInputValues.getNumeric("ipn_operator_left_y") > 0.0);
			case "st_climber_retract":
				return !(sharedInputValues.getNumeric("ipn_operator_left_y") < 0.0);

			// Turret
			case "st_turret_endgame":
				return false;

			// Sequences and Parallels
			case "pl_prime_to_shoot":
			case "pl_shoot":
			case "pl_collect_floor":
			case "pl_dejam":
				return true;

			// ------- Undefined states -------
			default:
				return state.isDone();
		}
	}
}
