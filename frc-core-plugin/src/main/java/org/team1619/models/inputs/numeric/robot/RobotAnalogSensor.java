package org.team1619.models.inputs.numeric.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import org.team1619.models.inputs.numeric.AnalogSensor;
import org.uacr.shared.abstractions.HardwareFactory;
import org.uacr.utilities.Config;

public class RobotAnalogSensor extends AnalogSensor {

    private final AnalogInput analogLogInput;

    public RobotAnalogSensor(Object name, Config config, HardwareFactory hardwareFactory) {
        super(name, config);

        analogLogInput = hardwareFactory.get(AnalogInput.class, port);
        analogLogInput.resetAccumulator();
    }

    @Override
    public double getVoltage() {
        return analogLogInput.getVoltage();
    }

    public double getAccumulatorCount() {
        return analogLogInput.getAccumulatorCount();
    }

    public double getAccumulatorValue() {
        return analogLogInput.getAccumulatorValue();
    }

    public double getValue() {
        return analogLogInput.getValue();
    }
}
