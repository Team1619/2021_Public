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
 * Sets the position of the servo that releases the collector
 */

public class Behavior_Collector_Servo_States implements Behavior {

	private static final Logger logger = LogManager.getLogger(Behavior_Collector_Servo_States.class);
	private static final Set<String> subsystems = Set.of("ss_collector_servo");

	private final InputValues sharedInputValues;
	private final OutputValues sharedOutputValues;
	private double servoPosition;

	public Behavior_Collector_Servo_States(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
		sharedInputValues = inputValues;
		sharedOutputValues = outputValues;
	}

	@Override
	public void initialize(String stateName, Config config) {
		logger.debug("Entering state {}", stateName);

		servoPosition = config.getDouble("servo_zero_offset", 0.0);
	}

	@Override
	public void update() {

		sharedOutputValues.setNumeric("opn_collector_servo", "", servoPosition);

	}

	@Override
	public void dispose() {
		sharedOutputValues.setNumeric("opn_collector_servo", "", 0.0);

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