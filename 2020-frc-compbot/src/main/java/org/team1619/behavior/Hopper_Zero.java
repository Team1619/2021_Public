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

public class Hopper_Zero implements Behavior {

	private static final Logger logger = LogManager.getLogger(Hopper_Zero.class);
	private static final Set<String> subsystems = Set.of("ss_hopper");

	private final InputValues sharedInputValues;
	private final OutputValues sharedOutputValues;
	private final Timer positionTimeoutTimer;
	private final Timer timeoutTimer;

	private int timeoutTime;
	private int positionTimeoutTime;
	private double zeroingThreshold;
	private double zeroSpeed;


	public Hopper_Zero(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
		sharedInputValues = inputValues;
		sharedOutputValues = outputValues;
		positionTimeoutTimer = new Timer();
		timeoutTimer = new Timer();

		timeoutTime = 0;
		positionTimeoutTime = 0;
		zeroingThreshold = 0.0;
		zeroSpeed = 0.0;

	}

	@Override
	public void initialize(String stateName, Config config) {
		logger.debug("Entering state {}", stateName);

		zeroingThreshold = config.getDouble("zeroing_threshold");
		timeoutTime = config.getInt("timeout_time");
		positionTimeoutTime = config.getInt("position_timeout_time");
		zeroSpeed = config.getDouble("zero_speed");

		timeoutTimer.reset();
		positionTimeoutTimer.reset();
		timeoutTimer.start(timeoutTime);
		positionTimeoutTimer.start(positionTimeoutTime);

		sharedOutputValues.setNumeric("opn_hopper", "percent", zeroSpeed);
	}

	@Override
	public void update() {

		// If hopper has not been zeroed, move the motor slowly until it reaches the zeroing threshold.
		if (!sharedInputValues.getBoolean("ipb_hopper_has_been_zeroed")) {

//			// If the position timeout timer ends and the hopper's position is not within the zeroing threshold, flag the motor as zeroed and send an error to the console
//			if (positionTimeoutTimer.isDone()) {
//				positionTimeoutTimer.reset();
//				sharedOutputValues.setOutputFlag("opn_hopper", "zero");
//				logger.error("Hopper Zero -> Position Timed out");
//				sharedOutputValues.setMotorOutputValue("opn_hopper", "percent", 0.0);
//			} else if (sharedInputValues.getBoolean("ipb_hopper_home_switch")) {
//				sharedOutputValues.setMotorOutputValue("opn_hopper", "percent", 0.0);
//				sharedOutputValues.setOutputFlag("opn_hopper", "zero");
//			}

			sharedOutputValues.setOutputFlag("opn_hopper", "zero");

			if (Math.abs(sharedInputValues.getNumeric("ipn_hopper_position")) < zeroingThreshold) {
				logger.debug("Hopper Zero -> Zeroed");
				sharedInputValues.setBoolean("ipb_hopper_has_been_zeroed", true);
			}
		}
	}


	@Override
	public void dispose() {

	}

	@Override
	public boolean isDone() {

		if (timeoutTimer.isDone() && !sharedInputValues.getBoolean("ipb_hopper_has_been_zeroed")) {
			timeoutTimer.reset();
			logger.error("Hopper zero -> Timed out");
		}
		return !sharedInputValues.getBoolean("ipb_hopper_has_been_zeroed") || timeoutTimer.isDone();


	}

	@Override
	public Set<String> getSubsystems() {
		return subsystems;
	}
}