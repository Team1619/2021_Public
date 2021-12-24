package org.team1619.behavior;

import org.uacr.models.behavior.Behavior;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.shared.abstractions.OutputValues;
import org.uacr.shared.abstractions.RobotConfiguration;
import org.uacr.utilities.Config;
import org.uacr.utilities.closedloopcontroller.ClosedLoopController;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

import java.util.Map;
import java.util.Set;

public class Drivetrain_Limelight_Align implements Behavior {

	private static final Logger logger = LogManager.getLogger(Drivetrain_Limelight_Align.class);
	private static final Set<String> subsystems = Set.of("ss_drivetrain");

	private final InputValues sharedInputValues;
	private final OutputValues sharedOutputValues;
	private final ClosedLoopController alignmentController;
	private final String gearShiftButton;

	private String stateName;
	private String limelight;

	public Drivetrain_Limelight_Align(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
		sharedInputValues = inputValues;
		sharedOutputValues = outputValues;
		alignmentController = new ClosedLoopController("pr_drive_align");
		gearShiftButton = robotConfiguration.getString("global_drivetrain", "gear_shift_button");

		stateName = "Unknown";
		limelight = "";
	}

	@Override
	public void initialize(String stateName, Config config) {
		logger.debug("Entering state {}", stateName);
		this.stateName = stateName;

		limelight = config.getString("limelight");

		//sharedInputValues.setInputFlag(limelight, "wide");

		alignmentController.setProfile("drive");
		alignmentController.reset();
	}

	@Override
	public void update() {

		Map<String, Double> llValues = sharedInputValues.getVector(limelight);

		boolean hasTarget = llValues.getOrDefault("tv", 0.0) == 1;

		if (hasTarget) {
			double llTargetX = llValues.getOrDefault("tx", 0.0);
			double llTargetY = llValues.getOrDefault("ty", 0.0);

			double llTargetDistance = Math.pow(75, -(llTargetY - 0.4)) + 2;

//			if(Math.abs(llTargetX) < 0) {
//				sharedInputValues.setInputFlag(limelight, "narrow");
//			} else {
//				sharedInputValues.setInputFlag(limelight, "wide");
//			}

			sharedInputValues.setNumeric("ipn_ll_distance", llTargetDistance);

			double output = alignmentController.getWithPID(llTargetX);

			sharedOutputValues.setNumeric("opn_drivetrain_left", "velocity", -output, "pr_pure_pursuit");
			sharedOutputValues.setNumeric("opn_drivetrain_right", "velocity", output, "pr_pure_pursuit");
		} else {
			sharedOutputValues.setNumeric("opn_drivetrain_left", "velocity", 0, "pr_pure_pursuit");
			sharedOutputValues.setNumeric("opn_drivetrain_right", "velocity", 0, "pr_pure_pursuit");
		}
	}

	@Override
	public void dispose() {
		logger.trace("Leaving state {}", stateName);

		//sharedInputValues.setInputFlag(limelight, "wide");

		sharedOutputValues.setNumeric("opn_drivetrain_left", "velocity", 0, "pr_pure_pursuit");
		sharedOutputValues.setNumeric("opn_drivetrain_right", "velocity", 0, "pr_pure_pursuit");
	}

	@Override
	public boolean isDone() {
		return Math.abs(sharedInputValues.getVector(limelight).get("tx")) < 1.5;
	}

	@Override
	public Set<String> getSubsystems() {
		return subsystems;
	}
}