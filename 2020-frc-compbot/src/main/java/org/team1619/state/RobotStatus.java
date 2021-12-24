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

	private static final Logger logger = LogManager.getLogger(RobotStatus.class);

	private Queue<Double> limelightDistanceValues;
	private String limelightTurretPnp;
	private boolean ledOn;
	private double velocityAdjustment;
	private String microAdjustAxis;
	private String macroAdjustAxis;
	private final double yAxisScalar;

	private static final double TARGET_HEIGHT = 98.25 / 12; // feet
	private static final double LIMELIGHT_HEIGHT = 25.75 / 12; // feet
	private static final double LIMELIGHT_ANGLE = 18.8; // degrees off vertical

	public RobotStatus(InputValues inputValues, RobotConfiguration robotConfiguration) {
		super(inputValues, robotConfiguration);

		limelightDistanceValues = new LimitedSizeQueue<>(30);

		limelightTurretPnp = "pnp-secondary";

		ledOn = false;

		microAdjustAxis = robotConfiguration.getString("global_flywheel", "micro_adjust_axis");
		macroAdjustAxis = robotConfiguration.getString("global_flywheel", "macro_adjust_axis");
		yAxisScalar = robotConfiguration.getDouble("global_flywheel", "y_axis_scalar");
	}

	@Override
	public void initialize() {
		// Zero
		if (!sharedInputValues.getBoolean("ipb_robot_has_been_zeroed")) {
			sharedInputValues.setBoolean("ipb_drivetrain_has_been_zeroed", false);
			sharedInputValues.setBoolean("ipb_hopper_has_been_zeroed", false);
			sharedInputValues.setBoolean("ipb_elevator_has_been_zeroed", false);
			sharedInputValues.setBoolean("ipb_turret_has_been_zeroed", false);
			sharedInputValues.setBoolean("ipb_flywheel_has_been_zeroed", false);
			sharedInputValues.setBoolean("ipb_climber_has_been_zeroed", false);
			sharedInputValues.setBoolean("ipb_collector_has_been_zeroed", false);
			sharedInputValues.setInputFlag("ipv_navx", "zero");
		}

		sharedInputValues.setBoolean("ipb_use_navx_alignment", true);
		sharedInputValues.setBoolean("ipb_use_limelight_targeting", true);

		limelightTurretPnp = "pnp-secondary";
		sharedInputValues.setInputFlag("ipv_limelight_turret", "pnp-main");
		sharedInputValues.setInputFlag("ipv_limelight_turret", "led-off");
		ledOn = false;

		limelightDistanceValues = new LimitedSizeQueue<>(30);

		velocityAdjustment = 0.0;
	}

	@Override
	public void disabledUpdate() {
		if (sharedInputValues.getBooleanRisingEdge("ipb_driver_dpad_down")) {
			ledOn = !ledOn;
			if(ledOn) {
				sharedInputValues.setInputFlag("ipv_limelight_turret", "led-on");
			} else {
				sharedInputValues.setInputFlag("ipv_limelight_turret", "led-off");
			}
		}
	}

	@Override
	public void update() {

		if (sharedInputValues.getBooleanRisingEdge("ipb_operator_a")) {
			sharedInputValues.setBoolean("ipb_use_navx_alignment", !sharedInputValues.getBoolean("ipb_use_navx_alignment"));
		}

		if (sharedInputValues.getBooleanRisingEdge("ipb_operator_x")) {
			sharedInputValues.setBoolean("ipb_use_limelight_targeting", !sharedInputValues.getBoolean("ipb_use_limelight_targeting"));
		}

		if(sharedInputValues.getBooleanRisingEdge("ipb_operator_y"))
		{
			sharedInputValues.setNumeric("ipn_turret_alignment_adjustment", 0.0);
			velocityAdjustment = 0.0	;
		}

		velocityAdjustment += (sharedInputValues.getNumeric(macroAdjustAxis) * yAxisScalar) + sharedInputValues.getNumeric(microAdjustAxis);
		sharedInputValues.setNumeric("ipn_flywheel_velocity_adjustment", velocityAdjustment);

		Map<String, Double> turretLimelightValues = sharedInputValues.getVector("ipv_limelight_turret");

		sharedInputValues.setBoolean("ipb_turret_limelight_locked", turretLimelightValues.get("tv") > 0);

		sharedInputValues.setNumeric("ipn_drivetrain_temperature", (sharedInputValues.getNumeric("ipn_drivetrain_left_primary_temperature") + sharedInputValues.getNumeric("ipn_drivetrain_right_primary_temperature")) / 2);

		sharedInputValues.setBoolean("ipb_drivetrain_safe_temperature", sharedInputValues.getNumeric("ipn_drivetrain_left_primary_temperature") < 85 && sharedInputValues.getNumeric("ipn_drivetrain_right_primary_temperature") < 85);

		double totalAngleRadians = Math.toRadians(17.5 + turretLimelightValues.get("ty"));

		// (top of target height - limelight height) / tan(limelight angle + ty)
		double limelightDistance = ((TARGET_HEIGHT - LIMELIGHT_HEIGHT) / Math.tan(Math.toRadians(LIMELIGHT_ANGLE + turretLimelightValues.get("ty"))));

		// Make sure the limelight reading is within the correct range
		if (limelightDistance > 8 && limelightDistance < 30) {
			limelightDistanceValues.add(limelightDistance);
		}

		// Average 30 distance calculations to reduce variations from the limelight target jumping
		double totalDistance = 0.0;
		for (double limelightDistanceValue : limelightDistanceValues) {
			totalDistance += limelightDistanceValue;
		}
		sharedInputValues.setNumeric("ipn_limelight_distance", totalDistance / limelightDistanceValues.size());


		sharedInputValues.setBoolean("ipb_primed_to_shoot",
				sharedInputValues.getBoolean("ipb_flywheel_primed") &&
						sharedInputValues.getBoolean("ipb_turret_aligned"));

		if (!sharedInputValues.getBoolean("ipb_endgame_enabled")) {
			if (sharedInputValues.getBooleanRisingEdge("ipb_operator_dpad_down")) {
				if (limelightTurretPnp.equals("pnp-secondary")) {
					limelightTurretPnp = "pnp-main";
					sharedInputValues.setInputFlag("ipv_limelight_turret", "pnp-main");
				} else if (limelightTurretPnp.equals("pnp-main")) {
					limelightTurretPnp = "pnp-secondary";
					sharedInputValues.setInputFlag("ipv_limelight_turret", "pnp-secondary");
				}
			}
		} else if (limelightTurretPnp.equals("pnp-main")) {
			sharedInputValues.setInputFlag("ipv_limelight_turret", "pnp-secondary");
		}

		if (!sharedInputValues.getBoolean("ipb_robot_has_been_zeroed") &&
				sharedInputValues.getBoolean("ipb_drivetrain_has_been_zeroed") &&
				sharedInputValues.getBoolean("ipb_hopper_has_been_zeroed") &&
				sharedInputValues.getBoolean("ipb_elevator_has_been_zeroed") &&
				sharedInputValues.getBoolean("ipb_turret_has_been_zeroed") &&
				sharedInputValues.getBoolean("ipb_flywheel_has_been_zeroed") &&
				sharedInputValues.getBoolean("ipb_climber_has_been_zeroed") &&
				sharedInputValues.getBoolean("ipb_collector_has_been_zeroed")) {

			sharedInputValues.setBoolean("ipb_robot_has_been_zeroed", true);
		}
	}

	@Override
	public void dispose() {
		logger.debug("Flywheel Adjustment {}", sharedInputValues.getNumeric("ipn_flywheel_velocity_adjustment"));
		logger.debug("Turret Limelight Adjustment {}", sharedInputValues.getNumeric("ipn_turret_alignment_adjustment"));
	}
}
