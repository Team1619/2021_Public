package org.team1619.behavior;

import org.uacr.models.behavior.Behavior;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.shared.abstractions.OutputValues;
import org.uacr.shared.abstractions.RobotConfiguration;
import org.uacr.utilities.Config;
import org.uacr.utilities.Timer;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

import java.util.Map;
import java.util.Set;

/**
 * Zeros the drivetrain.
 */

public class Drivetrain_Zero implements Behavior {

	private static final Logger logger = LogManager.getLogger(Drivetrain_Zero.class);
	private static final Set<String> subsystems = Set.of("ss_drivetrain");

	private final InputValues sharedInputValues;
	private final OutputValues sharedOutputValues;
	private final Timer timeoutTimer;

	private int timeoutTime;
	private double zeroingThreshold;
	private String stateName;


	public Drivetrain_Zero(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
		sharedInputValues = inputValues;
		sharedOutputValues = outputValues;
		timeoutTimer = new Timer();

		timeoutTime = 1000;
		zeroingThreshold = 0.0;
		stateName = "unknown";
	}

	@Override
	public void initialize(String stateName, Config config) {
		logger.debug("Entering state {}", stateName);

		this.stateName = stateName;
		timeoutTime = config.getInt("timeout_time");
		zeroingThreshold = config.getDouble("zeroing_threshold");

		timeoutTimer.reset();
		timeoutTimer.start(timeoutTime);

		sharedOutputValues.setNumeric("opn_drivetrain_left", "percent", 0.0);
		sharedOutputValues.setNumeric("opn_drivetrain_right", "percent", 0.0);
	}

	@Override
	public void update() {

		Map<String, Double> odometryValues = sharedInputValues.getVector("ipv_odometry");

		if (!sharedInputValues.getBoolean("ipb_drivetrain_has_been_zeroed")) {
			sharedInputValues.setInputFlag("ipv_odometry", "zero");
			sharedOutputValues.setOutputFlag("opn_drivetrain_left", "zero");
			sharedOutputValues.setOutputFlag("opn_drivetrain_right", "zero");

			if (Math.abs(sharedInputValues.getNumeric("ipn_drivetrain_left_primary_position")) < zeroingThreshold
					&& Math.abs(sharedInputValues.getNumeric("ipn_drivetrain_right_primary_position")) < zeroingThreshold
					&& Math.abs(odometryValues.get("x")) < zeroingThreshold
					&& Math.abs(odometryValues.get("y")) < zeroingThreshold) {
				logger.debug("Drivetrain Zero -> Zeroed");
				sharedOutputValues.setNumeric("opn_drivetrain_left", "percent", 0);
				sharedOutputValues.setNumeric("opn_drivetrain_right", "percent", 0);
				sharedInputValues.setBoolean("ipb_drivetrain_has_been_zeroed", true);
			}
		}
	}

	@Override
	public void dispose() {
		logger.trace("Leaving state {}", stateName);
		sharedOutputValues.setNumeric("opn_drivetrain_left", "percent", 0);
		sharedOutputValues.setNumeric("opn_drivetrain_right", "percent", 0);
	}

	@Override
	public boolean isDone() {
		if (timeoutTimer.isDone() && !sharedInputValues.getBoolean("ipb_drivetrain_has_been_zeroed")) {
			timeoutTimer.reset();
			logger.error("Drivetrain Zero -> Timed Out");
		}
		return sharedInputValues.getBoolean("ipb_drivetrain_has_been_zeroed") || timeoutTimer.isDone();
	}

	@Override
	public Set<String> getSubsystems() {
		return subsystems;
	}

}
