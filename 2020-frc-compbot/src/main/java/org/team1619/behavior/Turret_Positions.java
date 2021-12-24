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
 * Behavior for Turret Positions - moves the turret to specific positions and allows for manual control once in those positions.
 */

public class Turret_Positions implements Behavior {

	private static final Logger logger = LogManager.getLogger(Turret_Positions.class);
	private static final Set<String> subsystems = Set.of("ss_turret");

	private final InputValues sharedInputValues;
	private final OutputValues sharedOutputValues;
	private final String macroAdjustAxis;
	private final String microAdjustAxis;
	private final double yAxisScalar;

	private boolean allowAdjust;
	private double position;
	private double positionLeft;
	private double positionRight;

	public Turret_Positions(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
		sharedInputValues = inputValues;
		sharedOutputValues = outputValues;

		macroAdjustAxis = robotConfiguration.getString("global_turret", "macro_axis");
		microAdjustAxis = robotConfiguration.getString("global_turret", "micro_axis");
		yAxisScalar = robotConfiguration.getDouble("global_turret", "y_axis_scalar");

		allowAdjust = true;
		position = 0.0;
		positionLeft = 0.0;
		positionRight = 0.0;

	}

	@Override
	public void initialize(String stateName, Config config) {
		logger.debug("Entering state {}", stateName);

		position = config.getDouble("position", 57.2);
		positionLeft = config.getDouble("position_left", 0.0);
		positionRight = config.getDouble("position_right", 0.0);
		allowAdjust = config.getBoolean("allow_adjust", true);
		if (positionLeft != 0.0 && positionRight != 0.0) {
			position = positionRight;
		}
	}

	@Override
	public void update() {
		double macroAdjust = sharedInputValues.getNumeric(macroAdjustAxis);
		double microAdjust = sharedInputValues.getNumeric(microAdjustAxis);

		if (allowAdjust) {
			position += macroAdjust + (microAdjust * yAxisScalar);
		}

		// Oscillates the turret when in dejam mode
		if (positionLeft != 0.0 && positionRight != 0.0) {
			if (sharedInputValues.getNumeric("ipn_turret_position") <= positionRight) {
				position = positionLeft;
			} else if (sharedInputValues.getNumeric("ipn_turret_position") >= positionLeft) {
				position = positionRight;
			}

		}
		sharedOutputValues.setNumeric("opn_turret", "motion_magic", position, "pr_position");
	}

	@Override
	public void dispose() {
		sharedOutputValues.setNumeric("opn_turret", "percent", 0.0);
	}

	@Override
	public boolean isDone() {
		return Math.abs(position - sharedInputValues.getNumeric("ipn_turret_position")) < 2.0;
	}

	@Override
	public Set<String> getSubsystems() {
		return subsystems;
	}
}