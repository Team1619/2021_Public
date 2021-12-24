package org.team1619.models.outputs.bool.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.team1619.models.outputs.bool.SolenoidDouble;
import org.uacr.shared.abstractions.HardwareFactory;
import org.uacr.utilities.Config;

public class RobotSolenoidDouble extends SolenoidDouble {

    private final DoubleSolenoid wpiSolenoid;

    public RobotSolenoidDouble(Object name, Config config, HardwareFactory hardwareFactory) {
        super(name, config);

        wpiSolenoid = hardwareFactory.get(DoubleSolenoid.class, deviceNumberPrimary, deviceNumberFollower);
        wpiSolenoid.set(DoubleSolenoid.Value.kOff);
    }

    @Override
    public void processFlag(String flag) {

    }

    @Override
    public void setHardware(boolean output) {
        if (output) {
            wpiSolenoid.set(DoubleSolenoid.Value.kForward);
        } else {
            wpiSolenoid.set(DoubleSolenoid.Value.kReverse);
        }
    }
}