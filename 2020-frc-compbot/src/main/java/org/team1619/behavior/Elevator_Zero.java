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
 * Zeroes the elevator
 */

public class Elevator_Zero implements Behavior {

	private static final Logger logger = LogManager.getLogger(Elevator_Zero.class);
	private static final Set<String> subsystems = Set.of("ss_elevator");

	private final InputValues sharedInputValues;
	private final OutputValues sharedOutputValues;
	private final Timer timeoutTimer;

	private double zeroingThreshold;
	private int timeoutTime;

	public Elevator_Zero(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
		sharedInputValues = inputValues;
		sharedOutputValues = outputValues;
		timeoutTimer = new Timer();

		zeroingThreshold = 0.0;
		timeoutTime = 0;
	}

	@Override
	public void initialize(String stateName, Config config) {
		logger.debug("Entering state {}", stateName);

		zeroingThreshold = config.getDouble("zeroing_threshold");
		timeoutTime = config.getInt("zeroing_timeout_time");

		timeoutTimer.reset();
		timeoutTimer.start(timeoutTime);

		sharedOutputValues.setNumeric("opn_elevator", "percent", 0.0);
		sharedOutputValues.setBoolean("opb_elevator_extend", false);
	}

	@Override
	public void update() {
		if (!sharedInputValues.getBoolean("ipb_elevator_has_been_zeroed")) {
			sharedOutputValues.setOutputFlag("opn_elevator", "zero");

			if (Math.abs(sharedInputValues.getNumeric("ipn_elevator_position")) < zeroingThreshold) {
				logger.debug("Elevator Zero -> Zeroed");
				sharedInputValues.setBoolean("ipb_elevator_has_been_zeroed", true);
			}
		}
	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean isDone() {
		if (timeoutTimer.isDone() && !sharedInputValues.getBoolean("ipb_elevator_has_been_zeroed")) {
			timeoutTimer.reset();
			logger.error("Elevator Zero -> Timed Out");
		}
		return sharedInputValues.getBoolean("ipb_elevator_has_been_zeroed") || timeoutTimer.isDone();
	}

	@Override
	public Set<String> getSubsystems() {
		return subsystems;
	}
}