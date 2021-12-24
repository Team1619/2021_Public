package org.team1619.behavior;

import org.uacr.shared.abstractions.InputValues;
import org.uacr.shared.abstractions.OutputValues;
import org.uacr.shared.abstractions.RobotConfiguration;
import org.uacr.utilities.Config;
import org.uacr.utilities.closedloopcontroller.ClosedLoopController;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;
import org.uacr.utilities.purepursuit.Vector;

import java.util.Map;


/**
 * Drives the robot in swerve mode, based on the joystick values.
 */

public class Behavior_Drivetrain_Swerve_Align extends BaseSwerve {

    private static final Logger LOGGER = LogManager.getLogger(Behavior_Drivetrain_Swerve_Align.class);
    private static final Vector ZERO_TRANSLATION = new Vector();

    private final ClosedLoopController headingController;

    private String stateName;

    private double targetHeading;

    public Behavior_Drivetrain_Swerve_Align(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
        super(inputValues, outputValues, config, robotConfiguration, true);

        headingController = new ClosedLoopController(robotConfiguration.getString("global_drivetrain_swerve", "heading_controller"));
        
        stateName = "Unknown";

        targetHeading = 0.0;
    }

    @Override
    public void initialize(String stateName, Config config) {
        LOGGER.debug("Entering state {}", stateName);
        this.stateName = stateName;

        stopModules();

        targetHeading = config.getDouble("target_heading", targetHeading);

        headingController.setProfile("align");
        headingController.set(targetHeading);
        headingController.reset();
    }

    @Override
    public void update() {
        Map<String, Double> odometryValues = sharedInputValues.getVector(odometry);

        double heading = odometryValues.get("heading");
        double pidOutput = headingController.getWithPID(heading);

        setModulePowers(ZERO_TRANSLATION, pidOutput);
    }

    @Override
    public void dispose() {
        LOGGER.trace("Leaving state {}", stateName);

        stopModules();
    }

    @Override
    public boolean isDone() {
        return false;
    }
}