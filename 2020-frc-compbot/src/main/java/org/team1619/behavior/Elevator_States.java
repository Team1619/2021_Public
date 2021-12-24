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
 * Controls elevator in the state interrupt system
 */

public class Elevator_States implements Behavior {

	private static final Logger logger = LogManager.getLogger(Elevator_States.class);
	private static final Set<String> subsystems = Set.of("ss_elevator");

	private final InputValues sharedInputValues;
	private final OutputValues sharedOutputValues;
	private final Timer isDoneTimer;

	private boolean readBeamSensor;
	private boolean waitForFlywheels;
	private double elevatorSpeed;
	private boolean elevatorExtend;
	private String stateName;

	public Elevator_States(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
		sharedInputValues = inputValues;
		sharedOutputValues = outputValues;
		isDoneTimer = new Timer();

		readBeamSensor = false;
		waitForFlywheels = false;
		elevatorSpeed = 0.0;
		stateName = "";
	}

	@Override
	public void initialize(String stateName, Config config) {
		logger.debug("Entering state {}", stateName);

		readBeamSensor = config.getBoolean("read_beam_sensor", false);
		waitForFlywheels = config.getBoolean("wait_for_flywheels", false);
		elevatorSpeed = config.getDouble("speed");
		elevatorExtend = config.getBoolean("elevator_extend", false);
		this.stateName = stateName;

		isDoneTimer.reset();
		isDoneTimer.start(1000);
	}

	@Override
	public void update() {
		sharedOutputValues.setNumeric("opn_elevator", "percent", elevatorSpeed);
		sharedOutputValues.setBoolean("opb_elevator_extend", elevatorExtend);
	}

	@Override
	public void dispose() {
		logger.trace("Leaving state {}", stateName);

		sharedOutputValues.setNumeric("opn_elevator", "percent", 0.0);
		sharedOutputValues.setBoolean("opb_elevator_extend", false);
	}

	@Override
	public boolean isDone() {
		if (readBeamSensor) {
			if (sharedInputValues.getBoolean("ipb_beam_sensor_override")) {
				return false;
			}
			return sharedInputValues.getBoolean("ipb_elevator_beam_sensor");
		} else if (waitForFlywheels) {
			return sharedInputValues.getNumeric("ipn_flywheel_primary_velocity") <= 100;
		} else {
			return true;
		}
	}

	@Override
	public Set<String> getSubsystems() {
		return subsystems;
	}
}