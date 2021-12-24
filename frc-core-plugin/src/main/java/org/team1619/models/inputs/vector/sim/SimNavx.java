package org.team1619.models.inputs.vector.sim;

import org.team1619.models.inputs.vector.Navx;
import org.uacr.shared.abstractions.EventBus;
import org.uacr.utilities.Config;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

import java.util.HashMap;
import java.util.Map;

public class SimNavx extends Navx {

    private static final Logger logger = LogManager.getLogger(SimNavx.class);

    private final SimInputVectorListener listener;

    private double navxYaw;
    private double navxRoll;
    private double navxPitch;
    private double navxCompass;
    private double navxAngle;
    private double navxFusedHeading;
    private double navxAccelX;
    private double navxAccelY;
    private double navxAccelZ;


    public SimNavx(EventBus eventBus, Object name, Config config) {
        super(name, config);

        navxYaw = 0.0;
        navxRoll = 0.0;
        navxPitch = 0.0;
        navxCompass = 0.0;
        navxAngle = 0.0;
        navxFusedHeading = 0.0;
        navxAccelX = 0.0;
        navxAccelY = 0.0;
        navxAccelX = 0.0;

        listener = new SimInputVectorListener(eventBus, name, Map.of("yaw", navxYaw, "roll", navxRoll, "pitch", navxPitch, "compass", navxCompass, "angle", navxAngle, "fused_heading", navxFusedHeading, "accel_x", navxAccelX, "accel_y", navxAccelY, "accel_z", navxAccelZ));
    }

    protected Map<String, Double> readHardware() {

        //Inverted
        navxYaw = getValue("yaw");
        navxRoll = getValue("roll");
        navxPitch = getValue("pitch");
        navxCompass = getValue("compass");
        navxAngle = getValue("angle");
        navxFusedHeading = getValue("fused_heading");
        navxAccelX = getValue("accel_x");
        navxAccelY = getValue("accel_y");
        navxAccelZ = getValue("accel_z");

        return Map.of("yaw", navxYaw, "roll", navxRoll, "pitch", navxPitch, "compass", navxCompass, "angle", navxAngle, "fused_heading", navxFusedHeading, "accel_x", navxAccelX, "accel_y", navxAccelY, "accel_z", navxAccelZ);
    }

    private double getValue(String name) {
        double value = isInverted.get(name) ? listener.get().get(name) * -1 : listener.get().get(name);
        return (isRadians.containsKey(name) && isRadians.get(name)) ? value * Math.PI / 180 : value;
    }

    protected void zeroYaw() {
        logger.debug("SimNavxInput -> Zeroing yaw");
        double yaw = navxValues.get("yaw");

        Map<String, Double> lastNavxValues = navxValues;
        navxValues = new HashMap<>();
        navxValues.putAll(lastNavxValues);

        navxValues.put("yaw", 0.0);
    }
}
