package org.team1619.models.inputs.vector;

import org.uacr.models.inputs.vector.InputVector;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.utilities.Config;
import org.uacr.utilities.Lists;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;
import org.uacr.utilities.purepursuit.Pose2d;
import org.uacr.utilities.purepursuit.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SwerveOdometryInput is a Inputvector which uses the navx and drive encoders,
 * to track the robots current position relative to its starting point
 *
 * @author Matthew Oates
 */

public class SwerveOdometry extends BaseOdometry {

    private final String navx;
    private final List<String> modulePositionInputs;
    private final List<String> moduleAngleInputs;
    private final List<Double> lastModulePositions;

    private Map<String, Double> navxValues;
    private double heading;

    public SwerveOdometry(Object name, Config config, InputValues inputValues) {
        super(name, config, inputValues, UpdateMode.DELTA_POSITION);

        navxValues = new HashMap<>();

        navx = config.getString("navx");

        modulePositionInputs = Lists.of(config.getString("front_right_position"), config.getString("front_left_position"),
                config.getString("back_left_position"), config.getString("back_right_position"));

        moduleAngleInputs = Lists.of(config.getString("front_right_angle"), config.getString("front_left_angle"),
                config.getString("back_left_angle"), config.getString("back_right_angle"));

        lastModulePositions = new ArrayList<>();
        lastModulePositions.addAll(List.of(0.0, 0.0, 0.0, 0.0));

        heading = 0;
    }

    @Override
    public void initialize() {
        // Read all inputs so that the deltas work if the wheels aren't zeroed.
        getModuleVector(0);
        getModuleVector(1);
        getModuleVector(2);
        getModuleVector(3);
    }

    @Override
    protected Pose2d getPositionUpdate() {
        heading = getHeading();

        // Add all the module motions together then divide by the number of module to get robot oriented motion.
        // Rotate the robot oriented motion by the robot heading to get the robot motion relative to the field.
        Vector totalWheelTranslation = new Vector(getModuleVector(0).add(getModuleVector(1)).add(getModuleVector(2)).add(getModuleVector(3)));
        Vector robotTranslation = totalWheelTranslation.scale(0.25);
        Vector rotatedRobotTranslation = robotTranslation.rotate(heading);

        return new Pose2d(rotatedRobotTranslation, heading);
    }

    @Override
    protected void zero() {
        initialize();
    }

    private double getHeading() {
        navxValues = sharedInputValues.getVector(navx);
        double heading = navxValues.getOrDefault("angle", 0.0);

        // Inverts the heading to so that positive angle is counterclockwise, this makes trig functions work properly
        heading = -heading;

        return heading;
    }

    public Vector getModuleVector(int module) {
        return new Vector(getModuleDistance(module), getModuleAngle(module));
    }

    public double getModuleDistance(int module) {
        // The change in module position over this frame.
        double position = sharedInputValues.getNumeric(modulePositionInputs.get(module));
        return position - lastModulePositions.set(module, position);
    }

    public double getModuleAngle(int module) {
        return sharedInputValues.getVector(moduleAngleInputs.get(module)).getOrDefault("absolute_position", 0.0);
    }
}
