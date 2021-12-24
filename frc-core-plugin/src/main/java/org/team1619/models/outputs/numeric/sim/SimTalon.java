package org.team1619.models.outputs.numeric.sim;

import org.team1619.models.inputs.numeric.sim.SimInputNumericListener;
import org.team1619.models.outputs.numeric.Talon;
import org.uacr.events.sim.SimInputNumericSetEvent;
import org.uacr.shared.abstractions.EventBus;
import org.uacr.shared.abstractions.HardwareFactory;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.utilities.Config;

import java.util.HashMap;
import java.util.Map;

/**
 * SimTalon extends Talon, and acts like talons in sim mode
 */

public class SimTalon extends Talon {

    private final SimInputNumericListener positionListener;
    private final SimInputNumericListener velocityListener;
    private final Integer motor;

    private double output = 0.0;

    public SimTalon(Object name, Config config, HardwareFactory hardwareFactory, EventBus eventBus, InputValues inputValues) {
        super(name, config, inputValues);

        positionListener = new SimInputNumericListener(eventBus, positionInputName);
        velocityListener = new SimInputNumericListener(eventBus, velocityInputName);

        // Included to mimic RobotTalon for testing
        motor = hardwareFactory.get(Integer.class, deviceNumber);
    }

    @Override
    public void processFlag(String flag) {
        if (flag.equals("zero")) {
            zeroSensor();
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

        switch (outputType) {
            case "percent":
            case "follower":
                output = outputValue;
                break;
            case "velocity":
            case "position":
            case "motion_magic":
                setProfile(profile);

                output = outputValue;
                break;
            default:
                throw new RuntimeException("No output type " + outputType + " for TalonSRX");
        }    }

    @Override
    public double getMotorCurrent() {
        return 0;
    }

    @Override
    public double getMotorTemperature() {
        return 0;
    }

    @Override
    public double getSensorPosition() {
        return positionListener.get();
    }

    @Override
    public double getSensorVelocity() {
        return velocityListener.get();
    }

    @Override
    public void zeroSensor() {
        positionListener.onInputNumericSet(new SimInputNumericSetEvent(positionInputName, 0));
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

        currentProfileName = profileName;
    }
}
