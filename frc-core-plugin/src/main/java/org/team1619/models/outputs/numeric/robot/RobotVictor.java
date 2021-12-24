package org.team1619.models.outputs.numeric.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FollowerType;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import org.team1619.models.outputs.numeric.Victor;
import org.uacr.shared.abstractions.HardwareFactory;
import org.uacr.shared.abstractions.ObjectsDirectory;
import org.uacr.utilities.Config;

import javax.annotation.Nullable;

/**
 * RobotVictor extends Victor, and controls victor motor controllers on the robot
 */

public class RobotVictor extends Victor {

    private final HardwareFactory hardwareFactory;
    private final VictorSPX motor;

    public RobotVictor(Object name, Config config, HardwareFactory hardwareFactory) {
        super(name, config);
        this.hardwareFactory = hardwareFactory;

        motor = hardwareFactory.get(VictorSPX.class, deviceNumber);

        motor.setInverted(isInverted);
        motor.setNeutralMode(isBrakeModeEnabled ? NeutralMode.Brake : NeutralMode.Coast);
    }

    @Override
    public void processFlag(String flag) {

    }

    @Override
    public void setHardware(String outputType, double outputValue, String profile) {
        switch (outputType) {
            case "percent":
                motor.set(ControlMode.PercentOutput, outputValue);
                break;
            case "follower":
                motor.follow(hardwareFactory.get(TalonSRX.class, (int) outputValue), FollowerType.PercentOutput);
                break;
            default:
                throw new RuntimeException("No output type " + outputType + " for VictorSPX");
        }
    }
}
