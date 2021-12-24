package org.team1619.behavior;

import org.uacr.models.behavior.Behavior;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.shared.abstractions.OutputValues;
import org.uacr.shared.abstractions.RobotConfiguration;
import org.uacr.utilities.Config;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

import java.util.*;

/**
 * Controls the flywheel, using the state interrupt system
 */

public class Flywheel_States implements Behavior {

	private static final Logger logger = LogManager.getLogger(Flywheel_States.class);
	private static final Set<String> subsystems = Set.of("ss_flywheel");

	private final InputValues sharedInputValues;
	private final OutputValues sharedOutputValues;
	private final ArrayList<Double> dataDistances;
	private final Map<Double, Integer> speedProfile;

	private boolean allowAdjust;
	private boolean coast;
	private boolean hasReachedTurboVelocity;
	private double initialVelocity;
	private double velocity;
	private double finalVelocityError;
	private double percentOutput;
	private double turboVelocityCutoff;
	private String limelight;
	private String velocityProfile;

	public Flywheel_States(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
		sharedInputValues = inputValues;
		sharedOutputValues = outputValues;
		dataDistances = new ArrayList<>();
		speedProfile = new HashMap<>();
		for (Object p : robotConfiguration.getMap("global_flywheel", "speed_profile").entrySet()) {
			Map.Entry<Double, Integer> point = (Map.Entry<Double, Integer>) p;
			dataDistances.add(point.getKey());
			speedProfile.put(point.getKey(), point.getValue());
		}

		Collections.sort(dataDistances);

		allowAdjust = false;
		coast = false;
		initialVelocity = 0.0;
		velocity = 0.0;
		finalVelocityError = 0.0;
		percentOutput = 0.0;
		turboVelocityCutoff = 0.0;
		limelight = "";
		velocityProfile = "none";
	}

	@Override
	public void initialize(String stateName, Config config) {
		logger.debug("Entering state {}", stateName);

		allowAdjust = config.getBoolean("allow_adjust", false);
		coast = config.getBoolean("coast", false);
		hasReachedTurboVelocity = false;


		initialVelocity = config.getDouble("velocity", 0.0);
		velocity = initialVelocity;
		finalVelocityError = config.getDouble("final_velocity_error", 500.0);
		percentOutput = config.getDouble("percent_output", 0.9);
		turboVelocityCutoff = config.getInt("turbo_velocity_cutoff", 0);
		limelight = config.getString("limelight", "none");
		velocityProfile = config.getString("velocity_profile", "none");
	}

	@Override
	public void update() {

		String outputType = "velocity";
		String motorMode = "brake";

		if (turboVelocityCutoff != 0 && percentOutput != 0.0 && sharedInputValues.getNumeric("ipn_flywheel_primary_velocity") < turboVelocityCutoff && !hasReachedTurboVelocity) {

			// If we haven't reached the turbo prime speed yet set the motor to the turbo prime output in percent mode
			outputType = "percent";
			velocity = percentOutput;
		} else if (!limelight.equals("none")) {

			// If the state should use the limelight use the distance to set the output
			hasReachedTurboVelocity = true;

			double targetDistance = sharedInputValues.getNumeric("ipn_odometry_distance");

			if (targetDistance > 0) {
				// ((top of target height - limelight height) / tan(limelight angle - (1/2 limelight FOV) + ty))

				if (dataDistances.size() < 1) {
					// If there are no data points set velocity to 0
					velocity = 0;
				} else if (dataDistances.size() == 1) {
					// If there is one data point set velocity to the value at the point
					velocity = speedProfile.get(dataDistances.get(0));
				} else if (targetDistance < dataDistances.get(0)) {
					// If we are closer than the closest data point set the velocity to the value at the closest point
					velocity = speedProfile.get(dataDistances.get(0));
				} else if (targetDistance > dataDistances.get(dataDistances.size() - 1)) {
					// If we are farther than the farthest data point set the velocity to the value at the farthest point
					velocity = speedProfile.get(dataDistances.get(dataDistances.size() - 1));
				} else {
					// We have at least one data point closer and one data point farther than our current position
					// interpolate speed between the points

					// Find the first distance value that is further than where we are
					int index = 1;
					for (; index < dataDistances.size(); index++) {
						if (dataDistances.get(index) > targetDistance) {
							break;
						}
					}

					// Get the slope between the previous distance and the next distance
					double slope = (speedProfile.get(dataDistances.get(index)) - speedProfile.get(dataDistances.get(index - 1))) / (dataDistances.get(index) - dataDistances.get(index - 1));

					// Calculate flywheel distance based on previous point and slope
					velocity = speedProfile.get(dataDistances.get(index - 1)) + (slope * (targetDistance - dataDistances.get(index - 1)));
				}
			} else {
				// Set the velocity to the initial velocity if we don't have a limelight target
				velocity = initialVelocity;
			}
		} else if (turboVelocityCutoff != 0) {

			// If turbo prime is up to speed but we don't h
			velocity = initialVelocity;
			hasReachedTurboVelocity = true;
		} else if (coast) {

			// If the state is coast set the output to 0 and the motor to coast so the flywheels spin down slowly
			velocity = 0.0;
			motorMode = "coast";
			outputType = "percent";
		} else {

			// If we are just running percent set the velocity to the initial velocity
			velocity = initialVelocity;
		}

		if (velocityProfile.equals("none")) {

			// If there isn't a velocity profile set put the motor in percent output
			outputType = "percent";
		}


		if (allowAdjust && hasReachedTurboVelocity) {
			velocity += sharedInputValues.getNumeric("ipn_flywheel_velocity_adjustment");
		}

		if (velocity > 8000) {
			velocity = 8000;
		}

		if (velocity < -1000) {
			velocity = -1000;
		}

		sharedInputValues.setBoolean("ipb_flywheel_primed", Math.abs(velocity - sharedInputValues.getNumeric("ipn_flywheel_primary_velocity")) <= finalVelocityError && velocity != 0.0 && hasReachedTurboVelocity);

		sharedOutputValues.setOutputFlag("opn_flywheel", motorMode);
		sharedOutputValues.setNumeric("opn_flywheel", outputType, velocity, velocityProfile);
	}

	@Override
	public void dispose() {
		sharedInputValues.setBoolean("ipb_flywheel_primed", false);
		logger.debug("Flywheel velocity = {}, Is Done {}", sharedInputValues.getNumeric("ipn_flywheel_primary_velocity"), isDone());
	}

	@Override
	public boolean isDone() {
		if (turboVelocityCutoff != 0) {
			return Math.abs(velocity - sharedInputValues.getNumeric("ipn_flywheel_primary_velocity")) <= finalVelocityError && hasReachedTurboVelocity;
		} else {
			return Math.abs(velocity - sharedInputValues.getNumeric("ipn_flywheel_primary_velocity")) <= finalVelocityError;
		}
	}

	@Override
	public Set<String> getSubsystems() {
		return subsystems;
	}
}