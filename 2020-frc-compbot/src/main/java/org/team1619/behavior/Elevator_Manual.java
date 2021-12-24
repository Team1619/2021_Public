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
 * Manual control for the elevator. Runs the rollers at a set speed, increments the set speed.
 **/

public class Elevator_Manual implements Behavior {

	private static final Logger logger = LogManager.getLogger(Elevator_Manual.class);
	private static final Set<String> subsystems = Set.of("ss_elevator");

	private final InputValues sharedInputValues;
	private final OutputValues sharedOutputValues;
	private final double elevatorIncrement;
	private final String enabledButton;
	private final String incrementUpButton;
	private final String incrementDownButton;

	private double currentSpeed;
	private double initialSpeed;

	public Elevator_Manual(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
		sharedInputValues = inputValues;
		sharedOutputValues = outputValues;
		enabledButton = robotConfiguration.getString("global_elevator", "enabled_button");
		incrementUpButton = robotConfiguration.getString("global_elevator", "increment_up_button");
		incrementDownButton = robotConfiguration.getString("global_elevator", "increment_down_button");
		elevatorIncrement = robotConfiguration.getDouble("global_elevator", "elevator_increment");

		initialSpeed = robotConfiguration.getDouble("global_elevator", "initial_speed");
		currentSpeed = initialSpeed;
	}

	@Override
	public void initialize(String stateName, Config config) {
		logger.debug("Entering state {}", stateName);

		currentSpeed = initialSpeed;
	}

	@Override
	public void update() {

		if (sharedInputValues.getBoolean("ipb_endgame_enabled")) {
			return;
		}

		boolean enabledButton = sharedInputValues.getBoolean(this.enabledButton);
		boolean incrementUpButton = sharedInputValues.getBooleanRisingEdge(this.incrementUpButton);
		boolean incrementDownButton = sharedInputValues.getBooleanRisingEdge(this.incrementDownButton);

		if (incrementUpButton) {
			currentSpeed += elevatorIncrement;
		}
		if (incrementDownButton) {
			currentSpeed -= elevatorIncrement;
		}
		if (currentSpeed >= 1) {
			currentSpeed = 1;
		}
		if (currentSpeed <= -1) {
			currentSpeed = -1;
		}
		sharedInputValues.setNumeric("ipn_elevator_current_speed", currentSpeed);

		if (enabledButton) {
			sharedOutputValues.setNumeric("opn_elevator", "percent", currentSpeed);
		} else {
			sharedOutputValues.setNumeric("opn_elevator", "percent", 0.0);
		}
	}

	@Override
	public void dispose() {
		sharedOutputValues.setNumeric("opn_elevator", "percent", 0.0);
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