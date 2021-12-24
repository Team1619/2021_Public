package org.team1619.models.outputs.numeric;

import org.uacr.shared.abstractions.InputValues;
import org.uacr.utilities.Config;

import java.util.HashMap;
import java.util.Map;

/**
 * Talon is a motor object, which is extended to control talons
 */

public abstract class Talon extends CTREMotor {

    protected final InputValues sharedInputValues;
    protected final String feedbackDevice;
    protected final String positionInputName;
    protected final String velocityInputName;
    protected final String currentInputName;
    protected final String temperatureInputName;
    protected final boolean hasEncoder;
    protected final boolean sensorInverted;
    protected final boolean readPosition;
    protected final boolean readVelocity;
    protected final boolean readCurrent;
    protected final boolean readTemperature;
    protected final boolean currentLimitEnabled;
    protected final boolean forwardLimitSwitchEnabled;
    protected final boolean reverseLimitSwitchEnabled;
    protected final boolean forwardLimitSwitchNormallyClosed;
    protected final boolean reverseLimitSwitchNormallyClosed;
    protected final int continuousCurrentLimitAmps;
    protected final int peakCurrentLimitAmps;
    protected final double peakCurrentDurationSeconds;
    protected final double percentScalar;
    protected final double positionScalar;
    protected final double velocityScalar;
    protected final Map<String, Map<String, Double>> profiles;

    protected String currentProfileName;

    public Talon(Object name, Config config, InputValues inputValues) {
        super(name, config);

        sharedInputValues = inputValues;

        feedbackDevice = config.getString("feedback_device", "");
        hasEncoder = !feedbackDevice.isEmpty();
        sensorInverted = config.getBoolean("sensor_inverted", false);
        readPosition = config.getBoolean("read_position", false);
        readVelocity = config.getBoolean("read_velocity", false);
        readCurrent = config.getBoolean("read_current", false);
        readTemperature = config.getBoolean("read_temperature", false);
        currentLimitEnabled = config.getBoolean("current_limit_enabled", false);
        forwardLimitSwitchEnabled = config.getBoolean("forward_limit_switch_enabled", false);
        reverseLimitSwitchEnabled = config.getBoolean("reverse_limit_switch_enabled", false);
        forwardLimitSwitchNormallyClosed = config.getBoolean("forward_limit_switch_normally_closed", false);
        reverseLimitSwitchNormallyClosed = config.getBoolean("reverse_limit_switch_normally_closed", false);
        continuousCurrentLimitAmps = config.getInt("continuous_current_limit_amps", 0);
        peakCurrentLimitAmps = config.getInt("peak_current_limit_amps", 0);
        percentScalar = config.getDouble("percent_scalar", 1.0);
        positionScalar = config.getDouble("position_scalar", 1.0);
        velocityScalar = config.getDouble("velocity_scalar", 1.0);
        // reads in value in milliseconds, CTRE requires read out in seconds converts milliseconds to seconds
        peakCurrentDurationSeconds = config.getInt("peak_current_duration_milliseconds", 0) / 1000.0;
        positionInputName = config.getString("position_input_name", name.toString().replaceFirst("opn_", "ipn_") + "_position");
        velocityInputName = config.getString("velocity_input_name", name.toString().replaceFirst("opn_", "ipn_") + "_velocity");
        currentInputName = config.getString("current_input_name", name.toString().replaceFirst("opn_", "ipn_") + "_current");
        temperatureInputName = config.getString("temperature_input_name", name.toString().replaceFirst("opn_", "ipn_") + "_temperature");

        if (!(config.get("profiles", new HashMap<>()) instanceof Map)) throw new RuntimeException();
        profiles = (Map<String, Map<String, Double>>) config.get("profiles", new HashMap<>());

        currentProfileName = "none";
    }

    // Read the encoder's position
    protected void readEncoderPosition() {
        if (!hasEncoder) {
            return;
        }
        sharedInputValues.setNumeric(positionInputName, getSensorPosition());
    }

    // Read the encoder's velocity
    protected void readEncoderVelocity() {
        if (!hasEncoder) {
            return;
        }
        sharedInputValues.setNumeric(velocityInputName, getSensorVelocity());
    }

    protected void readMotorCurrent() {
        sharedInputValues.setNumeric(currentInputName, getMotorCurrent());
    }

    protected void readMotorTemperature() {
        sharedInputValues.setNumeric(temperatureInputName, getMotorTemperature());
    }

    public abstract double getSensorPosition();

    public abstract double getSensorVelocity();

    public abstract double getMotorCurrent();

    public abstract double getMotorTemperature();

    public abstract void zeroSensor();
}
