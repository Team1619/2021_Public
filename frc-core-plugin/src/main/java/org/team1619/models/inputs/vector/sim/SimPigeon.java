package org.team1619.models.inputs.vector.sim;

import org.team1619.models.inputs.vector.Pigeon;
import org.uacr.shared.abstractions.EventBus;
import org.uacr.utilities.Config;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

import java.util.HashMap;
import java.util.Map;

public class SimPigeon extends Pigeon {

    private static final Logger sLogger = LogManager.getLogger(SimPigeon.class);

    private final SimInputVectorListener fListener;

    private double pigeonYaw;
    private double pigeonRoll;
    private double pigeonPitch;
    private double pigeonCompass;
    private double pigeonFusedHeading;
    private double pigeonAccelX;
    private double pigeonAccelY;
    private double pigeonAccelZ;
    private double pigeonAngle;


    public SimPigeon(EventBus eventBus, Object name, Config config) {
        super(name, config);

        pigeonYaw = 0.0;
        pigeonRoll = 0.0;
        pigeonPitch = 0.0;
        pigeonCompass = 0.0;
        pigeonFusedHeading = 0.0;
        pigeonAccelX = 0.0;
        pigeonAccelY = 0.0;
        pigeonAccelZ = 0.0;
        pigeonAngle = 0.0;

        fListener = new SimInputVectorListener(eventBus, name, Map.of("yaw", pigeonYaw, "roll", pigeonRoll, "pitch", pigeonPitch, "compass", pigeonCompass, "angle", pigeonAngle, "fused_heading", pigeonFusedHeading, "accel_x", pigeonAccelX, "accel_y", pigeonAccelY, "accel_z", pigeonAccelZ));
    }

    protected Map<String, Double> readHardware() {

        //Inverted
        pigeonYaw = getValue("yaw");
        pigeonRoll = getValue("roll");
        pigeonPitch = getValue("pitch");
        pigeonCompass = getValue("compass");
        pigeonAngle = getValue("angle");
        pigeonFusedHeading = getValue("fused_heading");
        pigeonAccelX = getValue("accel_x");
        pigeonAccelY = getValue("accel_y");
        pigeonAccelZ = getValue("accel_z");

        return Map.of("yaw", pigeonYaw, "roll", pigeonRoll, "pitch", pigeonPitch, "compass", pigeonCompass, "angle", pigeonAngle, "fused_heading", pigeonFusedHeading, "accel_x", pigeonAccelX, "accel_y", pigeonAccelY, "accel_z", pigeonAccelZ);
    }

    private double getValue(String name) {
        double value = isInverted.get(name) ? fListener.get().get(name) * -1 : fListener.get().get(name);
        return (isRaidans.containsKey(name) && isRaidans.get(name)) ? value * Math.PI / 180 : value;
    }

    protected void zeroYaw() {
        sLogger.debug("pigeonInput -> Zeroing yaw");
        pigeonValues.put("yaw", 0.0);
    }
}
