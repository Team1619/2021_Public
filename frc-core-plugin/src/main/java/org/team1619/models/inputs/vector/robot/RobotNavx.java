package org.team1619.models.inputs.vector.robot;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import org.team1619.models.inputs.vector.Navx;
import org.uacr.shared.abstractions.HardwareFactory;
import org.uacr.utilities.Config;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

import java.util.Map;

public class RobotNavx extends Navx {

    private static final Logger logger = LogManager.getLogger(RobotNavx.class);

    private final AHRS navx;

    private double navxYaw;
    private double navxRoll;
    private double navxPitch;
    private double navxCompass;
    private double navxAngle;
    private double navxFusedHeading;
    private double navxAccelX;
    private double navxAccelY;
    private double navxAccelZ;


    public RobotNavx(Object name, Config config, HardwareFactory hardwareFactory) {
        super(name, config);

        navx = hardwareFactory.get(AHRS.class, SPI.Port.kMXP);
        navx.zeroYaw();

        navxYaw = 0.0;
        navxRoll = 0.0;
        navxPitch = 0.0;
        navxCompass = 0.0;
        navxAngle = 0.0;
        navxFusedHeading = 0.0;
        navxAccelX = 0.0;
        navxAccelY = 0.0;
        navxAccelX = 0.0;
    }

    @Override
    protected void zeroYaw() {
        logger.debug("RobotNavxInput -> Zeroing yaw");
        navx.zeroYaw();
    }

    @Override
    protected Map<String, Double> readHardware() {

        // Inverted
        navxYaw = isInverted.get("yaw") ? navx.getYaw() * -1 : navx.getYaw();
        navxRoll = isInverted.get("roll") ? navx.getRoll() * -1 : navx.getRoll();
        navxPitch = isInverted.get("pitch") ? navx.getPitch() * -1 : navx.getPitch();
        navxCompass = isInverted.get("compass") ? 360 - navx.getCompassHeading() : navx.getCompassHeading();
        navxAngle = isInverted.get("angle") ? navx.getAngle() * -1 : navx.getAngle();
        navxFusedHeading = isInverted.get("fused_heading") ? 360 - navx.getFusedHeading() : navx.getFusedHeading();
        navxAccelX = isInverted.get("accel_x") ? navx.getRawAccelX() * -1 : navx.getRawAccelX();
        navxAccelY = isInverted.get("accel_y") ? navx.getRawAccelY() * -1 : navx.getRawAccelY();
        navxAccelZ = isInverted.get("accel_z") ? navx.getRawAccelZ() * -1 : navx.getRawAccelZ();

        //Radians
        navxYaw = isRadians.get("yaw") ? navxYaw * Math.PI / 180 : navxYaw;
        navxRoll = isRadians.get("roll") ? navxRoll * Math.PI / 180 : navxRoll;
        navxPitch = isRadians.get("pitch") ? navxPitch * Math.PI / 180 : navxPitch;
        navxCompass = isRadians.get("compass") ? navxCompass * Math.PI / 180 : navxCompass;
        navxAngle = isRadians.get("angle") ? navxAngle * Math.PI / 180 : navxAngle;
        navxFusedHeading = isRadians.get("fused_heading") ? navxFusedHeading * Math.PI / 180 : navxFusedHeading;

        return Map.of("yaw", navxYaw, "roll", navxRoll, "pitch", navxPitch, "compass", navxCompass, "angle", navxAngle, "fused_heading", navxFusedHeading, "accel_x", navxAccelX, "accel_y", navxAccelY, "accel_z", navxAccelZ);
    }
}
