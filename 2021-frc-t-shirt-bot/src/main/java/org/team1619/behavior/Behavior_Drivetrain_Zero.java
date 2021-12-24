package org.team1619.behavior;

import org.uacr.shared.abstractions.InputValues;
import org.uacr.shared.abstractions.OutputValues;
import org.uacr.shared.abstractions.RobotConfiguration;
import org.uacr.utilities.Config;
import org.uacr.utilities.Timer;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

import java.util.*;
import java.util.stream.Stream;

/**
 * Zeros the swerve modules
 */

public class Behavior_Drivetrain_Zero extends BaseSwerve {

	private static final Logger LOGGER = LogManager.getLogger(Behavior_Drivetrain_Zero.class);

	private final Timer timeoutTimer;
	private int timeoutTime;
	private double zeroingThreshold;

	public Behavior_Drivetrain_Zero(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
		super(inputValues, outputValues, config, robotConfiguration, true);

		timeoutTimer = new Timer();
		timeoutTime = 500;
		zeroingThreshold = 0.1;
	}

	@Override
	public void initialize(String stateName, Config config) {
		LOGGER.debug("Entering state {}", stateName);

		timeoutTime = config.getInt("timeout_time");
		zeroingThreshold = config.getDouble("zeroing_threshold");

		timeoutTimer.reset();
		timeoutTimer.start(timeoutTime);

		stopModules();

		sharedInputValues.setInputFlag(navx, "zero");
		sharedInputValues.setInputFlag("ipv_swerve_odometry", "zero");
		sharedInputValues.setInputFlag("ipv_rpi_odometry", "zero");
		sharedInputValues.setInputFlag(odometry, "zero");

		Stream.concat(angleOutputNames.stream(), speedOutputNames.stream()).forEach(output -> sharedOutputValues.setOutputFlag(output, "zero"));
	}

	@Override
	public void update() {
		if (!sharedInputValues.getBoolean("ipb_drivetrain_has_been_zeroed")) {

			double maxWheelPosition = positionInputNames.stream().mapToDouble(sharedInputValues::getNumeric).map(Math::abs).max().getAsDouble();

			if (maxWheelPosition < zeroingThreshold && Math.abs(sharedInputValues.getVector(navx).get("angle")) < zeroingThreshold) {

				sharedInputValues.setInputFlag("ipv_swerve_odometry", "zero");
				sharedInputValues.setInputFlag("ipv_rpi_odometry", "zero");
				sharedInputValues.setInputFlag(odometry, "zero");

				Map<String, Double> odometryValues = sharedInputValues.getVector(odometry);

				if(Math.abs(odometryValues.get("x")) < zeroingThreshold &&
						Math.abs(odometryValues.get("y")) < zeroingThreshold) {
					LOGGER.debug("Drivetrain Zero -> Zeroed");
					sharedInputValues.setBoolean("ipb_drivetrain_has_been_zeroed", true);
				}
			}

			if (timeoutTimer.isDone()) {
				LOGGER.error("Drivetrain Zero -> Timed Out");

				timeoutTimer.reset();
				sharedInputValues.setInputFlag(odometry, "zero");
				sharedInputValues.setBoolean("ipb_drivetrain_has_been_zeroed", true);
			}
		}
	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean isDone() {
		return sharedInputValues.getBoolean("ipb_drivetrain_has_been_zeroed");
	}
}