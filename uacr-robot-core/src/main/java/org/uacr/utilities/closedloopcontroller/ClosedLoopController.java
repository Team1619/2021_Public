package org.uacr.utilities.closedloopcontroller;

import org.uacr.models.exceptions.ConfigurationInvalidTypeException;
import org.uacr.utilities.Config;
import org.uacr.utilities.YamlConfigParser;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ClosedLoopController {

    private static final Map<String, Double> profileDefaults = new HashMap<>();

    static {
        profileDefaults.put("integral_range", -1.0);
        profileDefaults.put("max_integral", -1.0);
        profileDefaults.put("idle_output", 0.0);
    }

    private final YamlConfigParser yamlConfigParser;
    private final String type;
    private final String name;
    private final Map<String, ClosedLoopControllerProfile> profiles = new HashMap<>();
    @Nullable
    private ClosedLoopControllerProfile currentClosedLoopControllerProfile;
    private double setpoint = 0.0;
    private double integral = 0.0;
    private double previousError = 0.0;
    private long previousTime = -1;

    public ClosedLoopController(String name) {
        this.name = name;
        yamlConfigParser = new YamlConfigParser();
        yamlConfigParser.loadWithFolderName("closed-loop-profiles.yaml");

        Config config = yamlConfigParser.getConfig(name);
        type = config.getType();
        Object configProfiles = config.get("profiles");

        if (!(configProfiles instanceof Map)) {
            throw new ConfigurationInvalidTypeException("Map", "profiles", configProfiles);
        }

        Map<String, Map<String, Double>> pidProfiles = (Map<String, Map<String, Double>>) configProfiles;

        for (Map.Entry<String, Map<String, Double>> profile : pidProfiles.entrySet()) {
            Map<String, Double> PIDValues = new HashMap<>(profileDefaults);
            for (Map.Entry<String, Double> parameter : pidProfiles.get(profile.getKey()).entrySet()) {
                PIDValues.put(parameter.getKey(), parameter.getValue());
            }
            profiles.put(profile.getKey(), new ClosedLoopControllerProfile(profile.getKey(), PIDValues));
        }

    }

    public void setProfile(String name) {
        if (!profiles.containsKey(name)) {
            throw new RuntimeException(name + "does not contain a profile named '" + name + "'");
        }
        currentClosedLoopControllerProfile = profiles.get(name);
    }

    public void set(double setpoint) {
        this.setpoint = setpoint;
    }

    public void reset() {
        integral = 0.0;
        previousError = 0.0;
        previousTime = -1;
    }

    public double getSetpoint() {
        return setpoint;
    }

    public double getIntegral() {
        return integral;
    }

    public double getError(double measuredValue) {
        return setpoint - measuredValue;
    }

    public double getWithPID(double measuredValue) {
        assert currentClosedLoopControllerProfile != null;
        long time = System.currentTimeMillis();

        if (previousTime == -1) {
            previousTime = time;
        }

        double deltaTime = (time - previousTime) / 1000.0;

        double error = setpoint - measuredValue;

        boolean insideIntegralRange = (currentClosedLoopControllerProfile.getIntegralRange() == -1 || Math.abs(error) <= currentClosedLoopControllerProfile.getIntegralRange());

        if (insideIntegralRange) {
            integral += deltaTime * error;

            if (currentClosedLoopControllerProfile.getMaxIntegral() != -1) {
                if (integral < 0.0) {
                    integral = Math.max(integral, -currentClosedLoopControllerProfile.getMaxIntegral());
                } else {
                    integral = Math.min(integral, currentClosedLoopControllerProfile.getMaxIntegral());
                }
            }
        } else {
            integral = 0.0;
        }

        double deltaError = error - previousError;
        double derivative = deltaTime != 0.0 ? deltaError / deltaTime : Double.MAX_VALUE;

        if (derivative == Double.MAX_VALUE) {
            //	logger.warn("Derivative is at max value (no delta time) and will be multiplied by {}", currentProfile.d);
        }

        double p = currentClosedLoopControllerProfile.getP() * error;
        double i = currentClosedLoopControllerProfile.getI() * integral;
        double d = currentClosedLoopControllerProfile.getD() * derivative;

        double output = currentClosedLoopControllerProfile.getF() * setpoint + p + i + d + currentClosedLoopControllerProfile.getIdleOutput();

        previousTime = time;
        previousError = error;

        if (currentClosedLoopControllerProfile.hasForceCompensation()) {
            return Math.signum(output) * Math.min(Math.abs(output), currentClosedLoopControllerProfile.getMaxOutput()) + (currentClosedLoopControllerProfile.getKForceCompensation() * Math.sin(measuredValue));
        } else {
            return Math.signum(output) * Math.min(Math.abs(output), currentClosedLoopControllerProfile.getMaxOutput());
        }
    }

    public double get(double measuredValue, double acceleration) {
        assert currentClosedLoopControllerProfile != null;
        if (!currentClosedLoopControllerProfile.hasFeedForward()) {
            throw new RuntimeException("The profile provided must include feedforward constants 'ka' and 'kv'");
        }

        double pidValue = getWithPID(measuredValue);
        return currentClosedLoopControllerProfile.getKv() * getSetpoint() + currentClosedLoopControllerProfile.getka() * acceleration + pidValue;
    }

}