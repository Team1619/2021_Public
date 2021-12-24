package org.team1619.models.inputs.vector;

import org.uacr.shared.abstractions.InputValues;
import org.uacr.utilities.Config;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;
import org.uacr.utilities.purepursuit.Pose2d;
import org.uacr.utilities.purepursuit.Vector;

import java.util.HashMap;
import java.util.Map;

/**
 * OdometryInput is a Inputvector which uses the navx and drive encoders,
 * to track the robots current position relative to its starting point
 *
 * @author Matthew Oates
 */

public class Odometry extends BaseOdometry {

    private final String navx;
    private final String leftEncoder;
    private final String rightEncoder;

    private Map<String, Double> navxValues;
    private double leftPosition = 0;
    private double rightPosition = 0;
    private double heading = 0;

    public Odometry(Object name, Config config, InputValues inputValues) {
        super(name, config, inputValues, UpdateMode.DELTA_POSITION);

        navxValues = new HashMap<>();

        navx = config.getString("navx");

        leftEncoder = config.getString("left_encoder");
        rightEncoder = config.getString("right_encoder");
    }

    @Override
    public Pose2d getPositionUpdate() {
        heading = getHeading();

        double leftPosition = sharedInputValues.getNumeric(leftEncoder);
        double rightPosition = sharedInputValues.getNumeric(rightEncoder);

		/*
		"distance" is the straight line distance the robot has traveled since the last iteration

		We calculate the straight line distance the robot has traveled by averaging the left and right encoder deltas.
		This works because we take readings so frequently that curvature doesn't impact the straight line distance
		enough to accumulate much error.

		This could be improved later but in our testing it worked well.
		 */
        double distance = ((leftPosition - this.leftPosition) + (rightPosition - this.rightPosition)) / 2;

        this.leftPosition = leftPosition;
        this.rightPosition = rightPosition;

        return new Pose2d(new Vector(distance, heading), heading);
    }

    @Override
    protected void zero() {
        leftPosition = sharedInputValues.getNumeric(leftEncoder);
        rightPosition = sharedInputValues.getNumeric(rightEncoder);
    }

    private double getHeading() {
        navxValues = sharedInputValues.getVector(navx);
        double heading = navxValues.getOrDefault("angle", 0.0);

        // Inverts the heading to so that positive angle is counterclockwise, this makes trig functions work properly
        heading = -heading;

        return heading;
    }
}
