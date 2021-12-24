package org.team1619.models.outputs.bool.robot;

import edu.wpi.first.wpilibj.Solenoid;
import org.team1619.models.outputs.bool.SolenoidSingle;
import org.uacr.shared.abstractions.HardwareFactory;
import org.uacr.utilities.Config;

public class RobotSolenoidSingle extends SolenoidSingle {

    private final Solenoid wpiSolenoid;

    public RobotSolenoidSingle(Object name, Config config, HardwareFactory hardwareFactory) {
        super(name, config);

        wpiSolenoid = hardwareFactory.get(Solenoid.class, deviceNumber);
    }

    @Override
    public void processFlag(String flag) {

    }

    @Override
    public void setHardware(boolean output) {
        wpiSolenoid.set(output);
    }
}