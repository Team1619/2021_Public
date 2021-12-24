package org.team1619.models.inputs.vector;

import org.uacr.models.inputs.vector.InputVector;
import org.uacr.utilities.Config;

import java.util.HashMap;
import java.util.Map;

public abstract class Navx extends InputVector {

    protected Map<String, Boolean> isRadians;
    protected Map<String, Boolean> isInverted;
    protected Map<String, Double> navxValues;

    public Navx(Object name, Config config) {
        super(name, config);

        navxValues = new HashMap<>();

        //Is Inverted
        isInverted = new HashMap<>();
        isInverted.put("yaw", config.getBoolean("yaw_is_inverted", false));
        isInverted.put("roll", config.getBoolean("roll_is_inverted", false));
        isInverted.put("pitch", config.getBoolean("pitch_is_inverted", false));
        isInverted.put("compass", config.getBoolean("compass_is_inverted", false));
        isInverted.put("angle", config.getBoolean("angle_is_inverted", false));
        isInverted.put("fused_heading", config.getBoolean("fused_heading_is_inverted", false));
        isInverted.put("accel_x", config.getBoolean("accel_x_is_inverted", false));
        isInverted.put("accel_y", config.getBoolean("accel_y_is_inverted", false));
        isInverted.put("accel_z", config.getBoolean("accel_z_is_inverted", false));

        // Is radians
        isRadians = new HashMap<>();
        isRadians.put("yaw", config.getBoolean("yaw_is_radians", false));
        isRadians.put("roll", config.getBoolean("roll_is_radians", false));
        isRadians.put("pitch", config.getBoolean("pitch_is_radians", false));
        isRadians.put("compass", config.getBoolean("compass_is_radians", false));
        isRadians.put("angle", config.getBoolean("angle_is_radians", false));
        isRadians.put("fused_heading", config.getBoolean("fused_heading_is_radians", false));
    }

    @Override
    public void update() {
        navxValues = readHardware();
    }

    @Override
    public void initialize() {
        navxValues = Map.of("Yaw", 0.0, "roll", 0.0, "pitch", 0.0, "compass", 0.0, "angle", 0.0, "fused_heading", 0.0, "accel_x", 0.0, "accel_y", 0.0, "accel_z", 0.0);
    }

    @Override
    public Map<String, Double> get() {
        return navxValues;
    }

    public void processFlag(String flag) {
        if (flag.equals("zero")) {
            zeroYaw();
        }
    }


    protected abstract Map<String, Double> readHardware();

    protected abstract void zeroYaw();
}
