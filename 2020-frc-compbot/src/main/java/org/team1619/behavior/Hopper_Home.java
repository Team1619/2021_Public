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
 * Behavior bring the hopper to the home position
 */

public class Hopper_Home implements Behavior {

	private static final Logger logger = LogManager.getLogger(Hopper_Home.class);
	private static final Set<String> subsystems = Set.of("ss_hopper");

	private final InputValues sharedInputValues;
	private final OutputValues sharedOutputValues;
	private final HopperTimer spinUpTimer;
	private final Timer startMovingTimer;
	private final Timer dejamTimer;
	private final Timer timeoutTimer;

	private final int startMovingTimerLength;
	private final int dejamTimerLength;
	private int spinUpTimerLength;
	private int timeoutTime;
	private double rotationSpeed;
	private final double jamThreshold;
	private final double dejamSpeed;
	private final double minimumMotorOutputToPreventJam;


	public Hopper_Home(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
		sharedInputValues = inputValues;
		sharedOutputValues = outputValues;
		timeoutTimer = new Timer();
		startMovingTimer = new Timer();
		dejamTimer = new Timer();
		spinUpTimer = new HopperTimer(inputValues);
		jamThreshold = robotConfiguration.getDouble("global_hopper", "jam_threshold");
		dejamSpeed = robotConfiguration.getDouble("global_hopper", "dejam_speed");
		startMovingTimerLength = robotConfiguration.getInt("global_hopper", "start_moving_timer_length");
		dejamTimerLength = robotConfiguration.getInt("global_hopper", "dejam_timer_length");
		spinUpTimerLength = robotConfiguration.getInt("global_hopper", "spin_up_timer_length");
		minimumMotorOutputToPreventJam = robotConfiguration.getDouble("global_hopper", "minimum_motor_output_to_prevent_jam");


		timeoutTime = 0;
		rotationSpeed = 0.0;
	}

	@Override
	public void initialize(String stateName, Config config) {
		logger.debug("Entering state {}", stateName);

		rotationSpeed = config.getDouble("rotation_speed");
		timeoutTime = config.getInt("timeout_time");

		startMovingTimer.reset();
		timeoutTimer.reset();
		spinUpTimer.reset();
		startMovingTimer.start(startMovingTimerLength);
		timeoutTimer.start(timeoutTime);
		spinUpTimer.start(spinUpTimerLength);
	}

	@Override
	public void update() {
		//This logic causes the hopper to run backward if it's velocity is too low (it is jammed)
		//The start moving timer is so the hopper has time to start moving before we trip dejaming
		//The dejam timer is the time we run backwards when dejaming

		//If we jam and we have had time to start moving and we are commanding the hopper to move
		if (startMovingTimer.isDone() && Math.abs(sharedInputValues.getNumeric("ipn_hopper_velocity")) < jamThreshold && !dejamTimer.isStarted() && rotationSpeed > 0.0) {
			dejamTimer.reset();
			dejamTimer.start(dejamTimerLength);

			//If we are dejaming run backwards
		} else if (dejamTimer.isStarted() && !dejamTimer.isDone()) {
			sharedOutputValues.setNumeric("opn_hopper", "velocity", dejamSpeed, "pr_spin");

			// Clear all the timers when we finish
		} else if (dejamTimer.isDone()) {
			dejamTimer.reset();
			startMovingTimer.reset();
			startMovingTimer.start(startMovingTimerLength);

			// If we are not dejaming do as the state requests
		} else {

			if (rotationSpeed > 0.0) {
				//Scale motor speed to time elapsed, but never less than jam threshold as to not run dejam code
				double actualSpeed  = Math.max(rotationSpeed * spinUpTimer.getPercentDone(), minimumMotorOutputToPreventJam);
				sharedOutputValues.setNumeric("opn_hopper", "velocity", actualSpeed, "pr_spin");
			} else {
				sharedOutputValues.setNumeric("opn_hopper", "percent", rotationSpeed);
			}
		}
	}

	@Override
	public void dispose() {
		sharedOutputValues.setNumeric("opn_hopper", "percent", 0.0);
	}

	@Override
	public boolean isDone() {
		return rotationSpeed == 0.0 || timeoutTimer.isDone() || sharedInputValues.getBoolean("ipb_hopper_home_switch");
	}

	@Override
	public Set<String> getSubsystems() {
		return subsystems;
	}
}