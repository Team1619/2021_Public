package org.team1619.state;

import org.uacr.robot.AbstractRobotStatus;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.shared.abstractions.RobotConfiguration;
import org.uacr.utilities.LimitedSizeQueue;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

import java.util.Map;
import java.util.Queue;

/**
 * Sets flags and does global math and logic for competition bot
 */

public class RobotStatus extends AbstractRobotStatus {

	private static final Logger LOGGER = LogManager.getLogger(RobotStatus.class);

	private String limelight;

	public RobotStatus(InputValues inputValues, RobotConfiguration robotConfiguration) {
		super(inputValues, robotConfiguration);

		limelight = robotConfiguration.getString("global_drivetrain_swerve", "limelight");
	}

	@Override
	public void initialize() {
		// Zero
		if (!sharedInputValues.getBoolean("ipb_robot_has_been_zeroed")) {
			sharedInputValues.setBoolean("ipb_drivetrain_has_been_zeroed", false);
		}
	}

	@Override
	public void update() {
		if (!sharedInputValues.getBoolean("ipb_robot_has_been_zeroed") &&
				sharedInputValues.getBoolean("ipb_drivetrain_has_been_zeroed")) {
			sharedInputValues.setBoolean("ipb_robot_has_been_zeroed", true);
		}
	}

	public void disabledUpdate() {
		// jace and alex path no signaling test


		Map<String, Double> llValues = sharedInputValues.getVector(limelight);
//		String pathName;
//		boolean hasTarget = llValues.getOrDefault("tv", 0.0) == 1;
//		pathName = "st_drivetrain_gsc_b_red";
//		if (hasTarget) {
//			pathName = "st_drivetrain_gsc_a_red";

//			double llTargetX = llValues.getOrDefault("tx", 0.0);
//			if (llTargetX < 0) {
//				pathName = "sq_auto_gsc_a_red";
//			} else {
//				pathName = "sq_auto_gsc_b_red";
//			}
//		}

		sharedInputValues.setString("gsc_path", llValues.getOrDefault("tv", 0.0) > 0.0 ? "st_drivetrain_gsc_a_red" : "st_drivetrain_gsc_b_red");
	}

	@Override
	public void dispose() {

	}
}
