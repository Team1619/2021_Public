package org.team1619.models.inputs.bool.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import org.team1619.models.inputs.bool.DigitalSensor;
import org.uacr.shared.abstractions.HardwareFactory;
import org.uacr.utilities.Config;

public class RobotDigitalSensor extends DigitalSensor {

    private final DigitalInput sensor;

    public RobotDigitalSensor(Object name, Config config, HardwareFactory hardwareFactory) {
        super(name, config);

        sensor = hardwareFactory.get(DigitalInput.class, id);
    }

    @Override
    public boolean getDigitalInputValue() {
        return sensor.get();
    }
}
