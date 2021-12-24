package org.team1619.behavior;

import org.uacr.models.behavior.Behavior;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.shared.abstractions.OutputValues;
import org.uacr.shared.abstractions.RobotConfiguration;
import org.uacr.utilities.Config;
import org.uacr.utilities.Timer;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

import java.util.Set;

/**
 * Zeroes Flywheel in Sequence Mode
 */

public class Flywheel_Zero implements Behavior {

	private static final Logger logger = LogManager.getLogger(Flywheel_Zero.class);
	private static final Set<String> subsystems = Set.of("ss_flywheel");

	private final InputValues sharedInputValues;
	private final OutputValues sharedOutputValues;
	private final Timer timeoutTimer;

	private int timeoutTime;

	public Flywheel_Zero(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
		sharedInputValues = inputValues;
		sharedOutputValues = outputValues;
		timeoutTimer = new Timer();

		timeoutTime = 0;
	}

	@Override
	public void initialize(String stateName, Config config) {
		logger.debug("Entering state {}", stateName);

		timeoutTime = config.getInt("zero_timeout_time");

		timeoutTimer.reset();
		timeoutTimer.start(timeoutTime);

		sharedInputValues.setBoolean("ipb_flywheel_primed", false);
		sharedOutputValues.setNumeric("opn_flywheel", "percent", 0.0);
	}

	@Override
	public void update() {
		if (!sharedInputValues.getBoolean("ipb_flywheel_has_been_zeroed")) {
			sharedOutputValues.setOutputFlag("opn_flywheel", "zero");
			sharedInputValues.setBoolean("ipb_flywheel_has_been_zeroed", true);
		}
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isDone() {
		return (sharedInputValues.getBoolean("ipb_flywheel_has_been_zeroed") || timeoutTimer.isDone());
	}

	@Override
	public Set<String> getSubsystems() {
		return subsystems;
	}
}