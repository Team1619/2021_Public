package org.team1619.robot;

import org.team1619.models.inputs.bool.robot.RobotControllerButton;
import org.team1619.models.inputs.bool.robot.RobotDigitalSensor;
import org.team1619.models.inputs.bool.robot.RobotJoystickButton;
import org.team1619.models.inputs.numeric.robot.RobotAnalogSensor;
import org.team1619.models.inputs.numeric.robot.RobotControllerAxis;
import org.team1619.models.inputs.numeric.robot.RobotJoystickAxis;
import org.team1619.models.inputs.vector.NetworkTableOdometry;
import org.team1619.models.inputs.vector.Odometry;
import org.team1619.models.inputs.vector.OdometryFuser;
import org.team1619.models.inputs.vector.SwerveOdometry;
import org.team1619.models.inputs.vector.robot.*;
import org.team1619.models.inputs.vector.sim.SimNetworkTableReader;
import org.team1619.models.outputs.bool.robot.RobotSolenoidDouble;
import org.team1619.models.outputs.bool.robot.RobotSolenoidSingle;
import org.team1619.models.outputs.numeric.AbsoluteEncoderTalon;
import org.team1619.models.outputs.numeric.MotorGroup;
import org.team1619.models.outputs.numeric.robot.RobotRumble;
import org.team1619.models.outputs.numeric.robot.RobotServo;
import org.team1619.models.outputs.numeric.robot.RobotTalon;
import org.team1619.models.outputs.numeric.robot.RobotVictor;
import org.uacr.models.inputs.bool.InputBoolean;
import org.uacr.models.inputs.numeric.InputNumeric;
import org.uacr.models.inputs.vector.InputVector;
import org.uacr.models.outputs.bool.OutputBoolean;
import org.uacr.models.outputs.numeric.OutputNumeric;
import org.uacr.robot.AbstractModelFactory;
import org.uacr.shared.abstractions.*;
import org.uacr.utilities.Config;
import org.uacr.utilities.YamlConfigParser;
import org.uacr.utilities.injection.Inject;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

public class AbstractRobotModelFactory extends AbstractModelFactory {

    private static final Logger logger = LogManager.getLogger(AbstractRobotModelFactory.class);

    private final HardwareFactory sharedHardwareFactory;

    @Inject
    public AbstractRobotModelFactory(HardwareFactory hardwareFactory, InputValues inputValues, OutputValues outputValues, RobotConfiguration robotConfiguration, ObjectsDirectory objectsDirectory) {
        super(inputValues, outputValues, robotConfiguration, objectsDirectory);

        sharedHardwareFactory = hardwareFactory;
    }

    @Override
    public OutputNumeric createOutputNumeric(Object name, Config config, YamlConfigParser parser) {
        logger.trace("Creating output numeric '{}' of type '{}' with config '{}'", name, config.getType(), config.getData());

        switch (config.getType()) {
            case "talon":
                return new RobotTalon(name, config, sharedHardwareFactory, sharedInputValues);
            case "victor":
                return new RobotVictor(name, config, sharedHardwareFactory);
            case "motor_group":
                return new MotorGroup(name, config, parser, this);
            case "absolute_encoder_talon":
                return new AbsoluteEncoderTalon(name, config, parser, this, sharedInputValues);
            case "servo":
                return new RobotServo(name, config, sharedHardwareFactory);
            case "rumble":
                return new RobotRumble(name, config, sharedHardwareFactory);
            default:
                return super.createOutputNumeric(name, config, parser);
        }
    }

    @Override
    public OutputBoolean createOutputBoolean(Object name, Config config, YamlConfigParser parser) {
        logger.trace("Creating output boolean '{}' of type '{}' with config '{}'", name, config.getType(), config.getData());

        switch (config.getType()) {
            case "solenoid_single":
                return new RobotSolenoidSingle(name, config, sharedHardwareFactory);
            case "solenoid_double":
                return new RobotSolenoidDouble(name, config, sharedHardwareFactory);
            default:
                return super.createOutputBoolean(name, config, parser);
        }
    }

    @Override
    public InputBoolean createInputBoolean(Object name, Config config) {
        logger.trace("Creating input boolean '{}' of type '{}' with config '{}'", name, config.getType(), config.getData());

        switch (config.getType()) {
            case "joystick_button":
                return new RobotJoystickButton(name, config, sharedHardwareFactory);
            case "controller_button":
                return new RobotControllerButton(name, config, sharedHardwareFactory);
            case "digital_input":
                return new RobotDigitalSensor(name, config, sharedHardwareFactory);
            default:
                return super.createInputBoolean(name, config);
        }
    }

    @Override
    public InputNumeric createInputNumeric(Object name, Config config) {
        logger.trace("Creating input numeric '{}' of type '{}' with config '{}'", name, config.getType(), config.getData());

        switch (config.getType()) {
            case "joystick_axis":
                return new RobotJoystickAxis(name, config, sharedHardwareFactory);
            case "controller_axis":
                return new RobotControllerAxis(name, config, sharedHardwareFactory);
            case "analog_sensor":
                return new RobotAnalogSensor(name, config, sharedHardwareFactory);
            default:
                return super.createInputNumeric(name, config);
        }
    }

    @Override
    public InputVector createInputVector(Object name, Config config) {
        logger.trace("Creating input vector '{}' of type '{}' with config '{}'", name, config.getType(), config.getData());

        switch (config.getType()) {
            case "accelerometer_input":
                return new RobotAcceleration(name, config, sharedInputValues);
            case "odometry_input":
                return new Odometry(name, config, sharedInputValues);
            case "swerve_odometry_input":
                return new SwerveOdometry(name, config, sharedInputValues);
            case "limelight":
                return new RobotLimelight(name, config);
            case "network_table_reader":
                return new RobotNetworkTableReader(name, config);
            case "network_table_odometry":
                return new NetworkTableOdometry(name, config, sharedInputValues);
            case "odometry_fuser":
                return new OdometryFuser(name, config, sharedInputValues);
            case "navx":
                return new RobotNavx(name, config, sharedHardwareFactory);
            case "cancoder":
                return new RobotCanCoder(name,config,sharedHardwareFactory);
            case "pigeon":
                return new RobotPigeon(name, config, sharedHardwareFactory);
            default:
                return super.createInputVector(name, config);
        }
    }
}