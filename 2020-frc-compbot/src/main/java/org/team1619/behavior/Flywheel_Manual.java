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
 * Manual control for the flywheel. Runs the flywheel at a set speed. Micro adjust and a macro adjust the set speed.
 */

public class Flywheel_Manual implements Behavior {

	private static final Logger logger = LogManager.getLogger(Flywheel_Manual.class);
	private static final Set<String> subsystems = Set.of("ss_flywheel");

	private final InputValues sharedInputValues;
	private final OutputValues sharedOutputValues;
	private final String macroAdjustAxis;
	private final String microAdjustAxis;
	private final double yAxisScalar;

	private double currentSpeed;
	private double initialSpeed;


	public Flywheel_Manual(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
		sharedInputValues = inputValues;
		sharedOutputValues = outputValues;
		microAdjustAxis = robotConfiguration.getString("global_flywheel", "micro_adjust_axis");
		macroAdjustAxis = robotConfiguration.getString("global_flywheel", "macro_adjust_axis");
		yAxisScalar = robotConfiguration.getDouble("global_flywheel", "y_axis_scalar");

		initialSpeed = robotConfiguration.getDouble("global_flywheel", "initial_speed");
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

		double macroAdjust = sharedInputValues.getNumeric(macroAdjustAxis);
		double microAdjust = sharedInputValues.getNumeric(microAdjustAxis);
		currentSpeed += (macroAdjust * yAxisScalar) + microAdjust;

		if (currentSpeed >= 7000) {
			currentSpeed = 7000;
		}

		if (currentSpeed <= 0) {
			currentSpeed = 0;
		}

		sharedInputValues.setNumeric("ipn_flywheel_current_speed", currentSpeed);
		sharedOutputValues.setNumeric("opn_flywheel", "velocity", currentSpeed, "pr_shoot");
	}

	@Override
	public void dispose() {
		sharedOutputValues.setNumeric("opn_flywheel", "percent", 0.0);

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