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
 * Zeros the climber subsystem
 */

public class Climber_Zero implements Behavior {

	private static final Logger logger = LogManager.getLogger(Climber_Zero.class);
	private static final Set<String> subsystems = Set.of("ss_climber");

	private final InputValues sharedInputValues;
	private final OutputValues sharedOutputValues;

	private double zeroingThreshold;

	public Climber_Zero(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
		sharedInputValues = inputValues;
		sharedOutputValues = outputValues;

		zeroingThreshold = 0.0;
	}

	@Override
	public void initialize(String stateName, Config config) {
		logger.debug("Entering state {}", stateName);

		zeroingThreshold = config.getDouble("zeroing_threshold");

		sharedOutputValues.setBoolean("opb_climber_ratchet", true);
		sharedOutputValues.setNumeric("opn_climber_winch", "percent", 0.0);
	}

	@Override
	public void update() {
		if (!sharedInputValues.getBoolean("ipb_climber_has_been_zeroed")) {
			sharedOutputValues.setOutputFlag("opn_climber_winch", "zero");
			sharedOutputValues.setNumeric("opn_climber_winch", "percent", 0.0);
			sharedOutputValues.setBoolean("opb_climber_ratchet", true);

			if (Math.abs(sharedInputValues.getNumeric("ipn_climber_winch_position")) < zeroingThreshold) {
				logger.debug("Climber Zero -> Zeroed");
				sharedInputValues.setBoolean("ipb_climber_has_been_zeroed", true);
			}
		}
	}

	@Override
	public void dispose() {
		sharedOutputValues.setNumeric("opn_climber_winch", "percent", 0.0);
		sharedOutputValues.setBoolean("opb_climber_ratchet", true);
	}

	@Override
	public boolean isDone() {
		return sharedInputValues.getBoolean("ipb_climber_has_been_zeroed");
	}

	@Override
	public Set<String> getSubsystems() {
		return subsystems;
	}
}