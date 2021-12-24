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
 * Manual control for the collector. Sets speeds for intake rollers, deploys/retracts collector.
 */

public class Collector_Manual implements Behavior {

	private static final Logger logger = LogManager.getLogger(Collector_Manual.class);
	private static final Set<String> subsystems = Set.of("ss_collector");

	private final InputValues sharedInputValues;
	private final OutputValues sharedOutputValues;
	private final String deployButton;
	private final String rollerSpeedButton;

	private int toggleStatus;
	private double rollerEjectSpeed;
	private double rollerIntakeSpeed;


	public Collector_Manual(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
		sharedInputValues = inputValues;
		sharedOutputValues = outputValues;
		deployButton = robotConfiguration.getString("global_collector", "deploy_button");
		rollerSpeedButton = robotConfiguration.getString("global_collector", "roller_speed_button");

		toggleStatus = 0;
		rollerEjectSpeed = 0.0;
		rollerIntakeSpeed = 0.0;
	}

	@Override
	public void initialize(String stateName, Config config) {
		logger.debug("Entering state {}", stateName);

		toggleStatus = 0;
		rollerIntakeSpeed = config.getDouble("roller_intake_speed");
		rollerEjectSpeed = config.getDouble("roller_eject_speed");

		sharedOutputValues.setBoolean("opb_collector_extend", true);
		sharedInputValues.setBoolean("ipb_collector_solenoid_position", true);
	}

	@Override
	public void update() {

		if (sharedInputValues.getBoolean("ipb_endgame_enabled")) {
			return;
		}

		boolean speedButton = sharedInputValues.getBooleanRisingEdge(rollerSpeedButton);
		boolean deployButton = sharedInputValues.getBooleanRisingEdge(this.deployButton);


		if (deployButton) {
			boolean isCollectorExtended = !sharedOutputValues.getBoolean("opb_collector_extend");
			sharedOutputValues.setBoolean("opb_collector_extend", isCollectorExtended);
			sharedInputValues.setBoolean("ipb_collector_solenoid_position", isCollectorExtended);
		}
		if (speedButton) {
			toggleStatus++;
		}
		if (toggleStatus >= 4) {
			toggleStatus = 0;
		}
		double rollersSpeed = 0;
		if (toggleStatus == 0 || toggleStatus == 2) {
			rollersSpeed = 0;
		} else if (toggleStatus == 1) {
			rollersSpeed = rollerIntakeSpeed;
		} else if (toggleStatus == 3) {
			rollersSpeed = rollerEjectSpeed;
		}

		sharedOutputValues.setNumeric("opn_collector_rollers", "percent", rollersSpeed);
		sharedInputValues.setNumeric("ipn_collector_rollers_speed", rollersSpeed);
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