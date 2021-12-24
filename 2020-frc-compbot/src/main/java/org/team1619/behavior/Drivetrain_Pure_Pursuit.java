package org.team1619.behavior;

import org.uacr.models.behavior.Behavior;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.shared.abstractions.OutputValues;
import org.uacr.shared.abstractions.RobotConfiguration;
import org.uacr.utilities.Config;
import org.uacr.utilities.WebDashboardGraphDataset;
import org.uacr.utilities.YamlConfigParser;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;
import org.uacr.utilities.purepursuit.Path;
import org.uacr.utilities.purepursuit.Point;
import org.uacr.utilities.purepursuit.Pose2d;
import org.uacr.utilities.purepursuit.ValueInterpolator;

import java.util.*;

/**
 * Uses the drivetrain to follow a path using the pure pursuit algorithm.
 */

public class Drivetrain_Pure_Pursuit implements Behavior {

	private static final Logger logger = LogManager.getLogger(Drivetrain_Pure_Pursuit.class);
	private static final Set<String> subsystems = Set.of("ss_drivetrain");

	private final InputValues sharedInputValues;
	private final OutputValues sharedOutputValues;
	private final Map<String, Path> paths;
	private final Map<String, Pose2d> startingPositions;
	private final double trackWidth;

	private Path currentPath;
	private Pose2d currentPosition;
	private Pose2d startingPosition;
	private boolean isFollowing;
	private boolean isReversed;
	private String stateName;
	private String pathName;

	public Drivetrain_Pure_Pursuit(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
		sharedInputValues = inputValues;
		sharedOutputValues = outputValues;
		paths = new HashMap<>();
		startingPositions = new HashMap<>();
		trackWidth = robotConfiguration.getDouble("global_drivetrain", "pure_pursuit_track_width");

		currentPath = new Path();
		currentPosition = new Pose2d();
		isFollowing = true;
		isReversed = false;
		stateName = "Unknown";
		pathName = "Unknown";

		// Creates and stores path objects from the paths config file, in the paths map
		YamlConfigParser yamlConfigParser = new YamlConfigParser();
		yamlConfigParser.loadWithFolderName("paths.yaml");

		Map<String, Map<String, Object>> paths = ((Map<String, Map<String, Object>>) yamlConfigParser.getData().get("path"));

		if (paths != null) {
			for (String pathName : paths.keySet()) {
				Config pathConfig = yamlConfigParser.getConfig(pathName);

				ArrayList<Point> points = new ArrayList<>();

				pathConfig.getList("path").forEach((p) -> {
					List point = (List) p;
					points.add(new Point(point.get(0) instanceof Integer ? (int) point.get(0) : (double) point.get(0), point.get(1) instanceof Integer ? (int) point.get(1) : (double) point.get(1)));
				});

				Path path = new Path(points);

				setupPathUsingConfig(path, pathConfig, yamlConfigParser);

				path.build();

				this.paths.put(pathName, path);

				double heading = 0.0;

				if(pathConfig.contains("start")) {
					Config startConfig = pathConfig.getSubConfig("start", "position");

					startingPositions.put(pathName, new Pose2d(startConfig.getDouble("x"), startConfig.getDouble("y"), startConfig.getDouble("heading", 0.0)));

					if(startConfig.contains("heading")) {
						heading = startConfig.getDouble("heading");
					}
				}

				sharedInputValues.getVector("ipv_odometry").put("heading", heading);
			}
		}
	}

	@Override
	public void initialize(String stateName, Config config) {
		logger.debug("Entering state {}", stateName);

		this.stateName = stateName;
		pathName = config.getString("path_name");
		isReversed = config.getBoolean("reversed", false);


		if (!paths.containsKey(pathName)) {
			logger.error("Path " + pathName + " doesn't exist");
			currentPath = new Path();
		} else {
			currentPath = paths.get(pathName);
		}

		if(startingPositions.containsKey(pathName)) {
			startingPosition = startingPositions.get(pathName);
			sharedInputValues.setVector("ipv_odometry_new_position", Map.of("x", startingPosition.getX(), "y", startingPosition.getY(), "heading", startingPosition.getHeading()));
		} else {
			startingPosition = null;
		}

		WebDashboardGraphDataset pathGraphDataset = new WebDashboardGraphDataset();

		for (Point point : currentPath.getPoints()) {
			pathGraphDataset.addPoint(point.getX(), point.getY());
		}

		sharedInputValues.setVector("gr_" + stateName, pathGraphDataset);
		currentPath.reset();
		isFollowing = true;

		sharedOutputValues.setBoolean("opb_drivetrain_gear_shifter", false);
		sharedInputValues.setBoolean("ipb_is_low_gear", false);
	}

	@Override
	public void update() {
		if (!isFollowing || !sharedInputValues.getBoolean("ipb_drivetrain_has_been_zeroed")) {
			sharedOutputValues.setNumeric("opn_drivetrain_left", "percent", 0.0);
			sharedOutputValues.setNumeric("opn_drivetrain_right", "percent", 0.0);
			return;
		}

		Map<String, Double> odometryValues = sharedInputValues.getVector("ipv_odometry");

		// Turns the odometry values into a Pose2d to pass to path methods
		currentPosition = new Pose2d(odometryValues.get("x"), odometryValues.get("y"), odometryValues.get("heading"));

		if(null != startingPosition && startingPosition.distance(currentPosition) > 1) {
			sharedOutputValues.setNumeric("opn_drivetrain_left", "percent", 0.0);
			sharedOutputValues.setNumeric("opn_drivetrain_right", "percent", 0.0);

			return;
		}
		startingPosition = null;

		Pose2d followPosition = currentPosition.clone();

		if (isReversed) {
			followPosition = new Pose2d(followPosition.getX(), followPosition.getY(), ((followPosition.getHeading() + 360) % 360) - 180);
		}

		int lookahead = currentPath.getLookAheadPointIndex(followPosition);
		int closest = currentPath.getClosestPointIndex(followPosition);

		if (lookahead == -1) {
			sharedOutputValues.setNumeric("opn_drivetrain_left", "percent", 0.0);
			sharedOutputValues.setNumeric("opn_drivetrain_right", "percent", 0.0);

			isFollowing = false;
			return;
		}

		// Uses the path object to calculate curvature and velocity values
		double velocity = currentPath.getPathPointVelocity(closest, followPosition);
		double curvature = currentPath.getCurvatureFromPathPoint(lookahead, followPosition);

		double left;
		double right;

		// Calculates wheel velocities based upon the curvature and velocity values calculated by the path
		if (isReversed) {
			left = -(velocity * ((1.5 - curvature * trackWidth) / 1.5));
			right = -(velocity * ((1.5 + curvature * trackWidth) / 1.5));
		} else {
			left = (velocity * ((1.5 + curvature * trackWidth) / 1.5));
			right = (velocity * ((1.5 - curvature * trackWidth) / 1.5));
		}

		//logger.debug("Velocity: {}, Curvature: {}, Left: {}, Right: {}, Lookahead: ({}, {})", velocity, curvature, left, right, currentPath.getPoint(lookahead).getX(), currentPath.getPoint(lookahead).getY());
		sharedInputValues.setVector("ipv_pure_pursuit", Map.of("velocity", velocity, "curvature", curvature, "left", left, "right", right, "cx", currentPath.getPoint(closest).getX(), "cy", currentPath.getPoint(closest).getY(), "lx", currentPath.getPoint(lookahead).getX(), "ly", currentPath.getPoint(lookahead).getY()));

		sharedOutputValues.setNumeric("opn_drivetrain_left", "velocity", left * 12, "pr_pure_pursuit");
		sharedOutputValues.setNumeric("opn_drivetrain_right", "velocity", right * 12, "pr_pure_pursuit");
	}

	@Override
	public void dispose() {
		logger.trace("Leaving state {}", stateName);
		sharedOutputValues.setNumeric("opn_drivetrain_left", "percent", 0.0);
		sharedOutputValues.setNumeric("opn_drivetrain_right", "percent", 0.0);
	}

	@Override
	public boolean isDone() {
		return !isFollowing;
	}

	@Override
	public Set<String> getSubsystems() {
		return subsystems;
	}

	private void setupPathUsingConfig(Path path, Config config, YamlConfigParser yamlConfigParser) {
		String modelName = config.getString("model", "none");

		if (!modelName.equals("none")) {
			Config model = yamlConfigParser.getConfig(modelName);

			setupPathUsingConfig(path, model, yamlConfigParser);
		}

		path.setPointSpacing(config.getDouble("spacing", path.getPointSpacing()));
		path.setPathSmoothing(config.getDouble("smoothing", path.getPathSmoothing()));
		path.setTurnSpeed(config.getDouble("turn_speed", path.getTurnSpeed()));
		path.setTrackingErrorSpeed(config.getDouble("tracking_error_speed", path.getTrackingErrorSpeed()));
		path.setMaxAcceleration(config.getDouble("max_acceleration", path.getMaxAcceleration()));
		path.setMaxDeceleration(config.getDouble("max_deceleration", path.getMaxDeceleration()));
		path.setMinSpeed(config.getDouble("min_speed", path.getMinSpeed()));
		path.setMaxSpeed(config.getDouble("max_speed", path.getMaxSpeed()));
		path.setLookAheadDistance(config.getDouble("look_ahead_distance", path.getLookAheadDistance()));
		path.setVelocityLookAheadPoints(config.getInt("velocity_look_ahead_points", path.getVelocityLookAheadPoints()));
	}
}