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
 * Zeros the servo that controls the release of the collector
 */

public class Behavior_Collector_Servo_Zero implements Behavior {

	private static final Logger logger = LogManager.getLogger(Behavior_Collector_Servo_Zero.class);
	private static final Set<String> subsystems = Set.of("ss_collector_servo");

	private final InputValues sharedInputValues;
	private final OutputValues sharedOutputValues;
	private Timer timer;


	private double servoZeroOffset;

	public Behavior_Collector_Servo_Zero(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
		sharedInputValues = inputValues;
		sharedOutputValues = outputValues;

		servoZeroOffset = 0.0;
		timer = new Timer();
	}

	@Override
	public void initialize(String stateName, Config config) {
		logger.debug("Entering state {}", stateName);

		servoZeroOffset = config.getDouble("servo_zero_offset", 0.0);
		long timerTimeout = config.getInt("timeout_time",500);
		timer.start(timerTimeout);
	}

	@Override
	public void update() {
		if (!sharedInputValues.getBoolean("ipb_collector_servo_has_been_zeroed")){
			sharedOutputValues.setNumeric("opn_collector_servo", "", servoZeroOffset);
		}

		if(timer.isDone()) {
			sharedInputValues.setBoolean("ipb_collector_servo_has_been_zeroed", true);
		}
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isDone() {
		return sharedInputValues.getBoolean("ipb_collector_servo_has_been_zeroed");
	}

	@Override
	public Set<String> getSubsystems() {
		return subsystems;
	}
}