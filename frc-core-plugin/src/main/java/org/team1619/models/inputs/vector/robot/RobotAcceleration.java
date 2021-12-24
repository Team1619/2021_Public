package org.team1619.models.inputs.vector.robot;

import org.team1619.models.inputs.vector.Accelerometer;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.utilities.Config;

import java.util.HashMap;
import java.util.Map;

public class RobotAcceleration extends Accelerometer {

    private final InputValues sharedInputValues;

    private Map<String, Double> navxValues;

    public RobotAcceleration(Object name, Config config, InputValues inputValues) {
        super(name, config);
        sharedInputValues = inputValues;
        navxValues = new HashMap<>();
    }

    @Override
    public Map<String, Double> getAcceleration() {
        navxValues = sharedInputValues.getVector("ipv_navx");

        double xAcceleration = navxValues.getOrDefault("accel_x", 0.0);
        double yAcceleration = navxValues.getOrDefault("accel_y", 0.0);
        double zAcceleration = navxValues.getOrDefault("accel_z", 0.0);

        return Map.of("xAcceleration", xAcceleration, "yAcceleration", yAcceleration, "zAcceleration", zAcceleration);
    }
}
