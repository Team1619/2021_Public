package org.team1619.behavior;

import org.uacr.models.behavior.Behavior;
import org.uacr.models.exceptions.ConfigurationException;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.shared.abstractions.OutputValues;
import org.uacr.shared.abstractions.RobotConfiguration;
import org.uacr.utilities.Config;
import org.uacr.utilities.closedloopcontroller.ClosedLoopController;
import org.uacr.utilities.purepursuit.Vector;
import org.uacr.utilities.purepursuit.VectorList;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BaseSwerve implements Behavior {

    protected static final Set<String> SUBSYSTEMS = Set.of("ss_drivetrain");

    protected final InputValues sharedInputValues;
    protected final OutputValues sharedOutputValues;

    protected final boolean useAngleDifferenceScalar;

    private final VectorList currentModuleVectors;

    protected final double maxModuleVelocity;
    protected final VectorList modulePositions;
    protected final VectorList moduleRotationDirections;

    protected final String navx;
    protected final String limelight;
    protected final String odometry;
    protected final List<String> angleInputNames;
    protected final List<String> positionInputNames;
    protected final List<String> angleOutputNames;
    protected final List<String> speedOutputNames;

    private final ClosedLoopController headingController;
    private String headingMode;

    protected double currentMaxModuleVelocity;

    public BaseSwerve(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration, boolean useAngleDifferenceScalar) {
        sharedInputValues = inputValues;
        sharedOutputValues = outputValues;

        this.useAngleDifferenceScalar = useAngleDifferenceScalar;

        maxModuleVelocity = robotConfiguration.getDouble("global_drivetrain_swerve", "max_module_velocity");
        currentMaxModuleVelocity = maxModuleVelocity;

        modulePositions = new VectorList(((List<List<Double>>) robotConfiguration.getList("global_drivetrain_swerve", "module_positions")).stream().map(Vector::new).collect(Collectors.toList()));

        currentModuleVectors = new VectorList(Collections.nCopies(4, new Vector()));

        // Create a set of vectors at right angles to the corners of the robot to use to calculate rotation vectors
        moduleRotationDirections = modulePositions.copy().normalizeAll().rotateAll(90);

        navx = robotConfiguration.getString("global_drivetrain_swerve", "navx");
        limelight = robotConfiguration.getString("global_drivetrain_swerve", "limelight");
        odometry = robotConfiguration.getString("global_drivetrain_swerve", "odometry");
        angleInputNames = robotConfiguration.getList("global_drivetrain_swerve", "input_angle_names");
        positionInputNames = robotConfiguration.getList("global_drivetrain_swerve", "input_position_names");
        angleOutputNames = robotConfiguration.getList("global_drivetrain_swerve", "output_angle_names");
        speedOutputNames = robotConfiguration.getList("global_drivetrain_swerve", "output_speed_names");

        headingController = new ClosedLoopController(robotConfiguration.getString("global_drivetrain_swerve", "heading_controller"));
        headingMode = "none";
    }

    protected void setModulePowers(Vector translation, VectorList moduleRotationDirections, double rotationSpeed) {
        setMotorPowers(calculateModuleVectors(translation, moduleRotationDirections, rotationSpeed));
    }

    protected void setModulePowers(Vector translation, double rotationSpeed) {
        setModulePowers(translation, moduleRotationDirections, rotationSpeed);
    }

    protected void setModulePowers(Vector translation, String headingMode, double headingOutput, String headingProfile) {

        switch (headingMode) {
            case "navx":
                if("none".equals(headingProfile)) {
                    headingProfile = "navx";
                }
                break;
            case "limelight":
                if("none".equals(headingProfile)) {
                    headingProfile = "limelight";
                }
                break;
            default:
                throw new ConfigurationException("Heading mode " + headingMode + " doesn't exist.");
        }

        headingController.setProfile(headingProfile);
        headingController.set(headingOutput);

        if(!this.headingMode.equals(headingMode)) {
            headingController.reset();
            this.headingMode = headingMode;
        }

        switch (headingMode) {
            case "navx":
                setModulePowers(translation, headingController.getWithPID(-sharedInputValues.getVector(navx).get("angle")));
                break;
            case "limelight":
                setModulePowers(translation, headingController.getWithPID(sharedInputValues.getVector(limelight).get("tx")));
                break;
        }
    }

    protected void setModulePowers(Vector translation, String headingMode, double headingOutput) {
        setModulePowers(translation, headingMode, headingOutput, "none");
    }

    protected void stopModules() {
        Stream.concat(angleOutputNames.stream(), speedOutputNames.stream()).forEach(output -> sharedOutputValues.setNumeric(output, "percent", 0.0));
    }

    protected VectorList calculateModuleVectors(Vector translation, VectorList moduleRotationDirections, double rotationSpeed) {
        for(int i = 0; i < 4; i++) {
            currentModuleVectors.set(i, calculateModuleVector(currentModuleVectors.get(i), angleInputNames.get(i), translation, moduleRotationDirections.get(i), rotationSpeed));
        }
        return currentModuleVectors.copy();
    }

    protected VectorList calculateModuleVectors(Vector translation, double rotationSpeed) {
        return calculateModuleVectors(translation, moduleRotationDirections, rotationSpeed);
    }

    protected Vector calculateModuleVector(Vector last, String currentAngleInput, Vector translation, Vector rotationDirection, Double rotationScalar) {
        double currentModuleAngle = sharedInputValues.getVector(currentAngleInput).get("absolute_position");
        Vector current = new Vector(last.magnitude(), currentModuleAngle);
        Vector target = new Vector(translation.add(rotationDirection.scale(rotationScalar)));

        // When the joysticks are idle, move the wheel angles to their rotation angle so the robot can spin instantly and move in any direction as quickly as possible.
        if (target.magnitude() == 0.0) {
            target = new Vector(0, last.angle());
        }

        // If the difference between the target angle and the actual angle is more than 90 degrees, rotate 180 degrees and reverse the motor direction.
        // Ramp up the wheel velocity as the actual angle get closer to the target angle. This prevents the robot from being pulled off course.
        // The cosine is raised to the power of 3 so that the ramp increases faster as the delta in the angle approaches zero.
        double directionScalar = Math.pow(Math.cos(Math.toRadians(target.angle() - current.angle())), 3);

        if(!useAngleDifferenceScalar) {
            directionScalar = Math.signum(directionScalar);
        }

        if (directionScalar < 0) {
            target = target.rotate(180);
        }

        return target.scale(directionScalar);
    }

    protected void setMotorPowers(VectorList moduleVectors) {
//        moduleVectors.autoScaleAll(VectorList.AutoScaleMode.SCALE_LARGEST_DOWN, 1.0);

        for(int m = 0; m < moduleVectors.size(); m++) {
            setMotorPower(m, moduleVectors.get(m));
        }
    }

    protected void setMotorPower(int moduleNumber, Vector moduleVector) {
        sharedOutputValues.setNumeric(angleOutputNames.get(moduleNumber), "absolute_position", moduleVector.angle(), "pr_drive");
        sharedOutputValues.setNumeric(speedOutputNames.get(moduleNumber), "velocity", moduleVector.magnitude() * currentMaxModuleVelocity, "pr_drive");
    }

    @Override
    public Set<String> getSubsystems() {
        return SUBSYSTEMS;
    }
}
