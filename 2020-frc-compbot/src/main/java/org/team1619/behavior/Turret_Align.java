package org.team1619.behavior;

import org.uacr.models.behavior.Behavior;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.shared.abstractions.OutputValues;
import org.uacr.shared.abstractions.RobotConfiguration;
import org.uacr.utilities.Config;
import org.uacr.utilities.Timer;
import org.uacr.utilities.closedloopcontroller.ClosedLoopController;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;
import org.uacr.utilities.purepursuit.Point;
import org.uacr.utilities.purepursuit.Vector;

import java.util.Map;
import java.util.Set;

public class Turret_Align implements Behavior {

	private static final Logger logger = LogManager.getLogger(Turret_Align.class);
	private static final Set<String> subsystems = Set.of("ss_turret");

	private final InputValues sharedInputValues;
	private final OutputValues sharedOutputValues;
	private final ClosedLoopController limelightAlignmentController;
	private final Timer limelightOdometryUpdateTimer;
	private final double yAxisScalar;
	private final double aimFromTrench;
	private final String macroAdjustAxis;
	private final String microAdjustAxis;

	private Vector lastLimelightOdometryPosition;
	private boolean hasPositionBeenUpdated;
	private boolean allowAdjust;
	private double turretAngleOffset;
	private double odometryXOffset;
	private double odometryYOffset;
	private double positionAdjustment;
	private double basePosition;
	private String limelight;
	private String odometry;
	private String turretPositionInput;
	private String stateName;

	public Turret_Align(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
		sharedInputValues = inputValues;
		sharedOutputValues = outputValues;
		limelightOdometryUpdateTimer = new Timer();
		macroAdjustAxis = robotConfiguration.getString("global_turret", "macro_axis");
		microAdjustAxis = robotConfiguration.getString("global_turret", "micro_axis");
		yAxisScalar = robotConfiguration.getDouble("global_turret", "y_axis_scalar");
		aimFromTrench = robotConfiguration.getDouble("global_turret", "aim_from_trench");
		limelightAlignmentController = new ClosedLoopController("pr_turret_align");

		sharedInputValues.setNumeric("ipn_turret_alignment_adjustment", 0.0);

		lastLimelightOdometryPosition = new Vector();
		hasPositionBeenUpdated = false;
		allowAdjust = true;
		turretAngleOffset = 0.0;
		odometryXOffset = 0.0;
		odometryYOffset = 0.0;
		basePosition = 0.0;
		limelight = "";
		odometry = "";
		turretPositionInput = "";
		stateName = "Unknown";
	}

	@Override
	public void initialize(String stateName, Config config) {
		logger.debug("Entering state {}", stateName);

		allowAdjust = config.getBoolean("allow_adjust", true);
		turretAngleOffset = config.getDouble("turret_angle_offset");
		limelight = config.getString("limelight");
		odometry = config.getString("odometry");
		turretPositionInput = config.getString("turret_position_input");
		this.stateName = stateName;

		lastLimelightOdometryPosition = new Vector();
		hasPositionBeenUpdated = false;
		basePosition = sharedInputValues.getNumeric(turretPositionInput);

		limelightAlignmentController.reset();
		limelightAlignmentController.setProfile("limelight");
		sharedInputValues.setInputFlag(limelight, "led-on");
	}

	@Override
	public void update() {

		if (sharedInputValues.getBoolean("ipb_use_limelight_targeting")) {
			sharedInputValues.setInputFlag(limelight, "led-on");
		} else {
			sharedInputValues.setInputFlag(limelight, "led-off");
		}

		boolean useNavxAlign = sharedInputValues.getBoolean("ipb_use_navx_alignment");

		boolean useLimelightAlign = sharedInputValues.getBoolean("ipb_use_limelight_targeting") && sharedInputValues.getBoolean("ipb_turret_limelight_locked");

		if (useNavxAlign || useLimelightAlign) {
			Map<String, Double> odometryValues = sharedInputValues.getVector(odometry);

			if (useLimelightAlign) {
				updateOdometryOffsetFromLimelight(sharedInputValues.getVector(limelight).getOrDefault("tx", 0.0));
			}

			sharedInputValues.setNumeric("ipn_odometry_distance", hasPositionBeenUpdated ? Math.hypot(odometryValues.get("y") + odometryYOffset, odometryValues.get("x") + odometryXOffset) / 12 : -1);

			sharedInputValues.setVector("ipv_turret_odometry", Map.of("x", odometryValues.get("x") + odometryXOffset, "y", odometryValues.get("y") + odometryYOffset));

			double angleFromTarget = Math.toDegrees(Math.atan2(odometryValues.get("y") + odometryYOffset, odometryValues.get("x") + odometryXOffset));

			if (!hasPositionBeenUpdated && useNavxAlign) {
				// Turns the turret directly toward the target wall until the odometry has been updated
				angleFromTarget = 180;
			}

			// Calculates the turret setpoint based on the heading angle to target and turret offset
			// -heading so the turret turns opposite the robot to cancel out robot angle
			// turret angle offset to account for 0 on the turret not being center
			// 540 to ensure that we never get a negative number and adds an extra 180 to account for the angle to target being 180 because it is actually the angle from target to robot
			// Modulo 360 to make sure the output is within the turret's range
			basePosition = ((-odometryValues.get("heading") + turretAngleOffset + angleFromTarget + 540) % 360);

			if (basePosition > 180 + turretAngleOffset / 2) {
				// Causes the turret position to wrap around halfway between the range of the turret
				basePosition = 0.0;
			}
		}

		double setpoint = basePosition;

		positionAdjustment = sharedInputValues.getNumeric("ipn_turret_alignment_adjustment");

		if (allowAdjust) {
			positionAdjustment += sharedInputValues.getNumeric(macroAdjustAxis) + (sharedInputValues.getNumeric(microAdjustAxis) * yAxisScalar);
			sharedInputValues.setNumeric("ipn_turret_alignment_adjustment", positionAdjustment);

			setpoint += positionAdjustment;
		}

		if (setpoint < 5) {
			setpoint = 5;
		}

		if (setpoint > 171) {
			setpoint = 171;
		}

		if (sharedInputValues.getBoolean("ipb_operator_b")){
			setpoint = aimFromTrench;
		}

		sharedInputValues.setBoolean("ipb_turret_aligned", Math.abs(setpoint - sharedInputValues.getNumeric(turretPositionInput)) < 2.0);

		sharedOutputValues.setNumeric("opn_turret", "position", setpoint, "pr_position_align");
	}

	@Override
	public void dispose() {
		logger.trace("Leaving state {}", stateName);

		sharedInputValues.setBoolean("ipb_turret_aligned", false);

		sharedInputValues.setInputFlag(limelight, "led-off");

		sharedOutputValues.setNumeric("opn_turret", "velocity", 0.0, "pr_align");
	}

	@Override
	public boolean isDone() {
		return sharedInputValues.getBoolean("ipb_turret_aligned");
	}

	@Override
	public Set<String> getSubsystems() {
		return subsystems;
	}

	public void updateOdometryOffsetFromLimelight(double limelightTargetX) {
		if (limelightOdometryUpdateTimer.isDone() || !hasPositionBeenUpdated) {
			// Updates once a second instead of every frame

			double limelightTargetDistance = sharedInputValues.getNumeric("ipn_limelight_distance") * 12;

			double outerGoalToRobotAngle = 180 - (sharedInputValues.getVector(odometry).get("heading") - limelightTargetX + sharedInputValues.getNumeric(turretPositionInput) - turretAngleOffset);

			Vector currentLimelightOdometryPosition = new Vector(new Point(Math.cos(Math.toRadians(outerGoalToRobotAngle)) * limelightTargetDistance, -Math.sin(Math.toRadians(outerGoalToRobotAngle)) * limelightTargetDistance));

			// Make sure the robot position hasn't jumped more than 60 inches (5 feet) since the last update to reduce the effect of occasionally targeting on defence
			if (lastLimelightOdometryPosition.distance(currentLimelightOdometryPosition) < 60) {

				Map<String, Double> odometryValues = sharedInputValues.getVector(odometry);

				odometryXOffset = currentLimelightOdometryPosition.getX() - odometryValues.get("x");
				odometryYOffset = currentLimelightOdometryPosition.getY() - odometryValues.get("y");

				hasPositionBeenUpdated = true;
			}

			lastLimelightOdometryPosition = currentLimelightOdometryPosition;

			limelightOdometryUpdateTimer.reset();
			limelightOdometryUpdateTimer.start(1000);
		}
	}
}