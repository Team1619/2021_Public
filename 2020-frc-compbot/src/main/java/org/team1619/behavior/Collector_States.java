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
 * Controls the collector subsystem using the state interrupt system.
 */

public class Collector_States implements Behavior {

	private static final Logger logger = LogManager.getLogger(Collector_States.class);
	private static final Set<String> subsystems = Set.of("ss_collector");

	private final InputValues sharedInputValues;
	private final OutputValues sharedOutputValues;

	private boolean solenoidPosition;
	private double rollerSpeed;

	public Collector_States(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
		sharedInputValues = inputValues;
		sharedOutputValues = outputValues;

		solenoidPosition = false;
		rollerSpeed = 0.0;

	}

	@Override
	public void initialize(String stateName, Config config) {
		logger.debug("Entering state {}", stateName);

		solenoidPosition = config.getBoolean("solenoid_position", false);
		rollerSpeed = config.getDouble("roller_speed", 0.0);
	}

	@Override
	public void update() {

		sharedOutputValues.setNumeric("opn_collector_rollers", "percent", rollerSpeed);
		sharedOutputValues.setBoolean("opb_collector_extend", solenoidPosition);
		sharedInputValues.setBoolean("ipb_collector_solenoid_position", solenoidPosition);
	}

	@Override
	public void dispose() {
		sharedOutputValues.setNumeric("opn_collector_rollers", "percent", 0.0);
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