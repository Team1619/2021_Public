package org.team1619.models.inputs.vector.robot;

import com.ctre.phoenix.sensors.PigeonIMU;
import org.team1619.models.inputs.vector.Pigeon;
import org.uacr.shared.abstractions.HardwareFactory;
import org.uacr.utilities.Config;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

import java.util.Map;

public class RobotPigeon extends Pigeon {

    private static final Logger sLogger = LogManager.getLogger(RobotPigeon.class);

    PigeonIMU pigeon = new PigeonIMU(deviceNumber);

    private double pigeonYaw;
    private double pigeonRoll;
    private double pigeonPitch;
    private double pigeonCompass;
    private double pigeonAngle;
    private double pigeonFusedHeading;
    private double pigeonAccelX;
    private double pigeonAccelY;
    private double pigeonAccelZ;


    public RobotPigeon(Object name, Config config, HardwareFactory hardwareFactory) {
        super(name, config);

        pigeonYaw = 0.0;
        pigeonRoll = 0.0;
        pigeonPitch = 0.0;
        pigeonCompass = 0.0;
        pigeonAngle = 0.0;
        pigeonFusedHeading = 0.0;
        pigeonAccelX = 0.0;
        pigeonAccelY = 0.0;
        pigeonAccelZ = 0.0;
    }

    @Override
    protected void zeroYaw() {
        sLogger.debug("RobotPigeonInput -> Zeroing yaw");
        pigeon.setYaw(0.0);
        pigeon.setFusedHeading(0.0);
    }

    @Override
    protected Map<String, Double> readHardware() {

        // Get Values
        double yawpitchroll[] = new double[3];
        pigeon.getYawPitchRoll(yawpitchroll);
        // yaw is the accumlative z axis - can exceed 360
        pigeonYaw = yawpitchroll[0];
        pigeonPitch = yawpitchroll[1];
        pigeonRoll = yawpitchroll[2];
        pigeonCompass = pigeon.getCompassHeading();
        pigeonFusedHeading = pigeon.getFusedHeading();
        double xyz[] = new double[3];
        pigeon.getAccelerometerAngles(xyz);
        pigeonAccelX = xyz[0];
        pigeonAccelY = xyz[1];
        pigeonAccelZ = xyz[2];

        // Convert accumlative yaw to +/- 180 angle to match the navX
        pigeonAngle = (pigeonYaw % 360.0);
        pigeonAngle = (pigeonAngle > 180) ? (pigeonAngle - 360) : pigeonAngle;
        pigeonAngle = (pigeonAngle < -180) ? (pigeonAngle + 360) : pigeonAngle;
        pigeonAngle = -1 * pigeonAngle;


        // Inverted
        pigeonYaw = isInverted.get("yaw") ? pigeonYaw * -1 : pigeonYaw;
        pigeonRoll = isInverted.get("roll") ? pigeonRoll * -1 : pigeonRoll;
        pigeonPitch = isInverted.get("pitch") ? pigeonPitch * -1 : pigeonPitch;
        pigeonCompass = isInverted.get("compass") ? 360 - pigeonCompass : pigeonCompass;
        pigeonAngle = isInverted.get("angle") ? pigeonAngle * -1 : pigeonAngle;
        pigeonFusedHeading = isInverted.get("fused_heading") ? 360 - pigeonFusedHeading : pigeonFusedHeading;
        pigeonAccelX = isInverted.get("accel_x") ? pigeonAccelX * -1 : pigeonAccelX;
        pigeonAccelY = isInverted.get("accel_y") ? pigeonAccelY * -1 : pigeonAccelY;
        pigeonAccelZ = isInverted.get("accel_z") ? pigeonAccelZ * -1 : pigeonAccelZ;

        //Radians
        pigeonYaw = isRaidans.get("yaw") ? pigeonYaw * Math.PI / 180 : pigeonYaw;
        pigeonRoll = isRaidans.get("roll") ? pigeonRoll * Math.PI / 180 : pigeonRoll;
        pigeonPitch = isRaidans.get("pitch") ? pigeonPitch * Math.PI / 180 : pigeonPitch;
        pigeonCompass = isRaidans.get("compass") ? pigeonCompass * Math.PI / 180 : pigeonCompass;
        pigeonAngle = isRaidans.get("angle") ? pigeonAngle * Math.PI / 180 : pigeonAngle;
        pigeonFusedHeading = isRaidans.get("fused_heading") ? pigeonFusedHeading * Math.PI / 180 : pigeonFusedHeading;

        return Map.of("yaw", pigeonYaw, "roll", pigeonRoll, "pitch", pigeonPitch, "compass", pigeonCompass, "angle", pigeonAngle, "fused_heading", pigeonFusedHeading, "accel_x", pigeonAccelX, "accel_y", pigeonAccelY, "accel_z", pigeonAccelZ);
    }
}
