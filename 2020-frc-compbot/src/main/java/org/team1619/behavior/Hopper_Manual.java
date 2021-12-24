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
 * Manual control for the hopper. Runs the hopper at a set speed, increments the set speed. Deploys the set speed.
 */

public class Hopper_Manual implements Behavior {

	private static final Logger logger = LogManager.getLogger(Hopper_Manual.class);
	private static final Set<String> subsystems = Set.of("ss_hopper");

	private final InputValues sharedInputValues;
	private final OutputValues sharedOutputValues;
	private final String incrementSpeedUpButton;
	private final String incrementSpeedDownButton;
	private final String enabledButton;
	private final double incrementHopper;

	private double initialSpeed;
	private double currentSpeed;


	public Hopper_Manual(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
		sharedInputValues = inputValues;
		sharedOutputValues = outputValues;
		incrementSpeedUpButton = robotConfiguration.getString("global_hopper", "increment_speed_up_button");
		incrementSpeedDownButton = robotConfiguration.getString("global_hopper", "increment_speed_down_button");
		enabledButton = robotConfiguration.getString("global_hopper", "enabled_button");
		incrementHopper = robotConfiguration.getDouble("global_hopper", "increment_speed");

		initialSpeed = robotConfiguration.getDouble("global_hopper", "initial_speed");
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

		boolean incrementUpButton = sharedInputValues.getBooleanRisingEdge(incrementSpeedUpButton);
		boolean incrementDownButton = sharedInputValues.getBooleanRisingEdge(incrementSpeedDownButton);
		boolean enabled = sharedInputValues.getBoolean(enabledButton);


		if (incrementUpButton) {
			currentSpeed += incrementHopper;
		}
		if (incrementDownButton) {
			currentSpeed -= incrementHopper;
		}
		if (currentSpeed >= 1) {
			currentSpeed = 1;
		}
		if (currentSpeed <= -1) {
			currentSpeed = -1;
		}

		sharedInputValues.setNumeric("ipn_hopper_current_speed", currentSpeed);

		if (enabled) {
			sharedOutputValues.setNumeric("opn_hopper", "percent", currentSpeed);
		} else {
			sharedOutputValues.setNumeric("opn_hopper", "percent", 0.0);
		}
	}

	@Override
	public void dispose() {
		sharedOutputValues.setNumeric("opn_hopper", "percent", 0.0);
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