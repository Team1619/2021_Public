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
 * Manual control for the turret. Runs turret left/right with micro/macro speeds.
 */

public class Turret_Manual implements Behavior {

	private static final Logger logger = LogManager.getLogger(Behavior_Example.class);
	private static final Set<String> subsystems = Set.of("ss_turret");

	private final InputValues sharedInputValues;
	private final OutputValues sharedOutputValues;
	private final String macroAdjustAxis;
	private final String microAdjustAxis;
	private final double yAxisScalar;

	private double rotationSpeed;

	public Turret_Manual(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
		sharedInputValues = inputValues;
		sharedOutputValues = outputValues;

		macroAdjustAxis = robotConfiguration.getString("global_turret", "macro_axis");
		microAdjustAxis = robotConfiguration.getString("global_turret", "micro_axis");
		yAxisScalar = robotConfiguration.getDouble("global_turret", "y_axis_scalar");

		rotationSpeed = 0.0;
	}

	@Override
	public void initialize(String stateName, Config config) {
		logger.debug("Entering state {}", stateName);

		rotationSpeed = 0.0;
	}

	@Override
	public void update() {

		if (sharedInputValues.getBoolean("ipb_endgame_enabled")) {
			return;
		}

		double macroAdjust = sharedInputValues.getNumeric(macroAdjustAxis);
		double microAdjust = sharedInputValues.getNumeric(microAdjustAxis);
		rotationSpeed = macroAdjust + (microAdjust * yAxisScalar);

		sharedOutputValues.setNumeric("opn_turret", "percent", rotationSpeed);
	}

	@Override
	public void dispose() {
		sharedOutputValues.setNumeric("opn_turret", "percent", 0.0);
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