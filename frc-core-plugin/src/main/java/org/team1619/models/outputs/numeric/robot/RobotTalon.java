package org.team1619.models.outputs.numeric.robot;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import org.team1619.models.outputs.numeric.Talon;
import org.uacr.shared.abstractions.HardwareFactory;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.utilities.Config;

/**
 * RobotTalon extends Talon, and controls talon motor controllers on the robot
 */

public class RobotTalon extends Talon {

    protected static final int CAN_TIMEOUT_MILLISECONDS = 10;

    protected final HardwareFactory hardwareFactory;
    protected final BaseTalon motor;
    protected final PowerDistributionPanel pdp;

    public RobotTalon(Object name, Config config, HardwareFactory hardwareFactory, InputValues inputValues) {
        super(name, config, inputValues);

        this.hardwareFactory = hardwareFactory;

        Class<? extends BaseTalon> motorType = config.getString("type", "srx").equalsIgnoreCase("fx") ? TalonFX.class : TalonSRX.class;

        motor = hardwareFactory.get(motorType, deviceNumber);
        pdp = hardwareFactory.get(PowerDistributionPanel.class);

        motor.configFactoryDefault(CAN_TIMEOUT_MILLISECONDS);

        motor.setInverted(isInverted);
        motor.setNeutralMode(isBrakeModeEnabled ? NeutralMode.Brake : NeutralMode.Coast);

        motor.setSensorPhase(sensorInverted);

        SupplyCurrentLimitConfiguration currentLimitConfiguration = new SupplyCurrentLimitConfiguration();
        currentLimitConfiguration.enable = currentLimitEnabled;
        currentLimitConfiguration.currentLimit = continuousCurrentLimitAmps;
        currentLimitConfiguration.triggerThresholdCurrent = peakCurrentLimitAmps;
        currentLimitConfiguration.triggerThresholdTime = peakCurrentDurationSeconds;
        motor.configSupplyCurrentLimit(currentLimitConfiguration, CAN_TIMEOUT_MILLISECONDS);

        if(hasEncoder) {
            switch (feedbackDevice) {
                case "quad_encoder":
                    motor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, CAN_TIMEOUT_MILLISECONDS);
                    break;
                case "internal_encoder":
                    motor.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 0, CAN_TIMEOUT_MILLISECONDS);
                    break;
                case "cancoder":
                    motor.configRemoteFeedbackFilter(config.getInt("encoder_number"), RemoteSensorSource.CANCoder, 0, CAN_TIMEOUT_MILLISECONDS);
                    motor.configSelectedFeedbackSensor(FeedbackDevice.RemoteSensor0, 0, CAN_TIMEOUT_MILLISECONDS);
                    break;
                case "remote_talon":
                    motor.configRemoteFeedbackFilter(config.getInt("encoder_number"), RemoteSensorSource.TalonSRX_SelectedSensor, 0, CAN_TIMEOUT_MILLISECONDS);
                    motor.configSelectedFeedbackSensor(FeedbackDevice.RemoteSensor0, 0, CAN_TIMEOUT_MILLISECONDS);
                    break;
                default:
                    throw new RuntimeException("Invalid configuration for Talon feedback device: " + feedbackDevice);
            }
        }

        motor.configForwardLimitSwitchSource(forwardLimitSwitchEnabled ? LimitSwitchSource.FeedbackConnector : LimitSwitchSource.Deactivated, forwardLimitSwitchNormallyClosed ? LimitSwitchNormal.NormallyClosed : LimitSwitchNormal.NormallyOpen, CAN_TIMEOUT_MILLISECONDS);
        motor.configReverseLimitSwitchSource(reverseLimitSwitchEnabled ? LimitSwitchSource.FeedbackConnector : LimitSwitchSource.Deactivated, reverseLimitSwitchNormallyClosed ? LimitSwitchNormal.NormallyClosed : LimitSwitchNormal.NormallyOpen, CAN_TIMEOUT_MILLISECONDS);
    }

    @Override
    public void processFlag(String flag) {
        if (flag.equals("zero")) {
            zeroSensor();
        }
        else if (flag.equals("coast")) {
            motor.setNeutralMode(NeutralMode.Coast);
        }
        else if (flag.equals("brake")) {
            motor.setNeutralMode(NeutralMode.Brake);
        }
    }

    @Override
    public void setHardware(String outputType, double outputValue, String profile) {

        if (readPosition) {
            readEncoderPosition();
        }
        if (readVelocity) {
            readEncoderVelocity();
        }
        if (readCurrent) {
            readMotorCurrent();
        }
        if (readTemperature) {
            readMotorTemperature();
        }

        switch (outputType) {
            case "percent":
                motor.set(ControlMode.PercentOutput, outputValue * percentScalar);
                break;
            case "follower":
                motor.follow(hardwareFactory.get(TalonSRX.class, (int) outputValue), FollowerType.PercentOutput);
                break;
            case "velocity":
                setProfile(profile);

                motor.set(ControlMode.Velocity, (outputValue / velocityScalar) / 10);
                break;
            case "position":
                setProfile(profile);

                motor.set(ControlMode.Position, outputValue / positionScalar);
                break;
            case "motion_magic":
                setProfile(profile);

                motor.set(ControlMode.MotionMagic, outputValue / positionScalar);
                break;
            default:
                throw new RuntimeException("No output type " + outputType + " for TalonSRX");
        }
    }

    @Override
    public double getSensorPosition() {
        return motor.getSelectedSensorPosition(0) * positionScalar;
    }

    @Override
    public double getSensorVelocity() {
        return motor.getSelectedSensorVelocity(0) * 10 * velocityScalar;
    }

    @Override
    public double getMotorCurrent() {
        return pdp.getCurrent(deviceNumber);
    }

    @Override
    public double getMotorTemperature() {
        return motor.getTemperature();
    }

    @Override
    public void zeroSensor() {
        motor.setSelectedSensorPosition(0, 0, CAN_TIMEOUT_MILLISECONDS);
    }

    private void setProfile(String profileName) {
        if (profileName.equals("none")) {
            throw new RuntimeException("PIDF Profile name must be specified");
        }

        if (profileName.equals(currentProfileName)) {
            return;
        }

        if (!profiles.containsKey(profileName)) {
            throw new RuntimeException("PIDF Profile " + profileName + " doesn't exist");
        }

        Config profile = new Config("pidf_config", profiles.get(profileName));

        motor.config_kP(0, profile.getDouble("p", 0.0), CAN_TIMEOUT_MILLISECONDS);
        motor.config_kI(0, profile.getDouble("i", 0.0), CAN_TIMEOUT_MILLISECONDS);
        motor.config_kD(0, profile.getDouble("d", 0.0), CAN_TIMEOUT_MILLISECONDS);
        motor.config_kF(0, profile.getDouble("f", 0.0), CAN_TIMEOUT_MILLISECONDS);
        motor.configMotionAcceleration(profile.getInt("acceleration", 0), CAN_TIMEOUT_MILLISECONDS);
        motor.configMotionCruiseVelocity(profile.getInt("cruise_velocity", 0), CAN_TIMEOUT_MILLISECONDS);
        //S Curve values tend to cause slamming and jerking
        motor.configMotionSCurveStrength(profile.getInt("s_curve", 0), CAN_TIMEOUT_MILLISECONDS);

        currentProfileName = profileName;
    }
}
