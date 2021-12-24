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
 * Zeros the turret
 */

public class Turret_Zero implements Behavior {

	private static final Logger logger = LogManager.getLogger(Turret_Zero.class);
	private static final Set<String> subsystems = Set.of("ss_turret");

	private final InputValues sharedInputValues;
	private final OutputValues sharedOutputValues;
	private final Timer timeoutTimer;
	private final Timer positionTimeoutTimer;
	private final Timer startMovingTimer;
	private final Timer zeroSettleTimer;

	private boolean isReadyToZero;
	private int timeoutTime;
	private int positionTimeoutTime;
	private int startMovingTime;
	private int zeroSettleTime;
	private double zeroingSpeed;
	private double zeroVelocityThreshold;
	private double zeroingThreshold;


	public Turret_Zero(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
		sharedInputValues = inputValues;
		sharedOutputValues = outputValues;
		startMovingTimer = new Timer();
		zeroSettleTimer = new Timer();
		timeoutTimer = new Timer();
		positionTimeoutTimer = new Timer();

		isReadyToZero = false;
		timeoutTime = 0;
		positionTimeoutTime = 0;
		startMovingTime = 0;
		zeroSettleTime = 0;
		zeroingSpeed = 0.0;
		zeroVelocityThreshold = 0.0;
		zeroingThreshold = 0.0;
	}

	@Override
	public void initialize(String stateName, Config config) {
		logger.debug("Entering state {}", stateName);

		isReadyToZero = false;
		timeoutTime = config.getInt("zeroing_timeout_time");
		positionTimeoutTime = config.getInt("zeroing_position_timeout_time");
		startMovingTime = config.getInt("zeroing_start_moving_time");
		zeroSettleTime = config.getInt("zeroing_settle_time");
		zeroingThreshold = config.getDouble("zeroing_threshold");
		zeroingSpeed = config.getDouble("zeroing_speed");
		zeroVelocityThreshold = config.getDouble("zeroing_velocity_threshold");

		timeoutTimer.reset();
		startMovingTimer.reset();
		positionTimeoutTimer.reset();
		timeoutTimer.start(timeoutTime);
		startMovingTimer.start(startMovingTime);
		positionTimeoutTimer.start(positionTimeoutTime);

		sharedInputValues.setBoolean("ipb_turret_aligned", false);
		sharedOutputValues.setNumeric("opn_turret", "percent", zeroingSpeed);
	}

	@Override
	public void update() {
		//Zeroes the turret if the velocity is less than the set threshold
		if (!sharedInputValues.getBoolean("ipb_turret_has_been_zeroed")) {

			//If velocity isn't below the desired threshold before the timer runs out zero it anyway
			if (positionTimeoutTimer.isDone()) {
				positionTimeoutTimer.reset();
				sharedOutputValues.setOutputFlag("opn_turret", "zero");
				isReadyToZero = true;
				sharedOutputValues.setNumeric("opn_turret", "percent", 0.0);
				logger.error("Turret Zero -> Position Timed Out");

			} else if (startMovingTimer.isDone() && !zeroSettleTimer.isStarted() && Math.abs(sharedInputValues.getNumeric("ipn_turret_velocity")) < zeroVelocityThreshold) {
				//If the velocity is below the threshold zero the turret
				sharedOutputValues.setNumeric("opn_turret", "percent", 0.0);
				zeroSettleTimer.reset();
				zeroSettleTimer.start(zeroSettleTime);
			}

			if (zeroSettleTimer.isDone()) {
				isReadyToZero = true;
				sharedOutputValues.setOutputFlag("opn_turret", "zero");
			}

			//Prints the message that the turret has been zeroed if the rotation position is less than the threshold
			if (isReadyToZero && Math.abs(sharedInputValues.getNumeric("ipn_turret_position")) < zeroingThreshold) {
				logger.debug("Turret Zero -> Zeroed");
				zeroSettleTimer.reset();
				sharedOutputValues.setNumeric("opn_turret", "percent", 0.0);
				sharedInputValues.setBoolean("ipb_turret_has_been_zeroed", true);
			}
		}
	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean isDone() {
		if (timeoutTimer.isDone() && !sharedInputValues.getBoolean("ipb_turret_has_been_zeroed")) {
			timeoutTimer.reset();
			logger.error("Turret Zero -> Timed Out");
		}
		return sharedInputValues.getBoolean("ipb_turret_has_been_zeroed") || timeoutTimer.isDone();
	}

	@Override
	public Set<String> getSubsystems() {
		return subsystems;
	}
}