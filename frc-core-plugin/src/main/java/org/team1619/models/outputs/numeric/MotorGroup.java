package org.team1619.models.outputs.numeric;

import org.uacr.models.exceptions.ConfigurationInvalidTypeException;
import org.uacr.models.outputs.numeric.OutputNumeric;
import org.uacr.robot.AbstractModelFactory;
import org.uacr.utilities.Config;
import org.uacr.utilities.YamlConfigParser;

import java.util.HashSet;
import java.util.Set;

/**
 * Motor group holds a reference to a master CTRE motor, and all its follower CTRE motors.
 * A motor group acts just like a regular motor to the framework, but sets all the followers into follower mode,
 * and passes all setHardware calls to the master motor.
 */

public class MotorGroup extends OutputNumeric {

    private final CTREMotor master;
    private final Set<CTREMotor> slaves = new HashSet<>();

    public MotorGroup(Object name, Config config, YamlConfigParser parser, AbstractModelFactory modelFactory) {
        super(name, config);

        String master = config.getString("master");
        OutputNumeric motor = modelFactory.createOutputNumeric(master, parser.getConfig(master), parser);
        if (!(motor instanceof CTREMotor)) {
            throw new ConfigurationInvalidTypeException("Talon", "master", motor);
        }
        this.master = (CTREMotor) motor;

        for (Object slaveName : config.getList("followers")) {
            motor = modelFactory.createOutputNumeric(slaveName, parser.getConfig(slaveName), parser);
            if (!(motor instanceof CTREMotor)) {
                throw new ConfigurationInvalidTypeException("Motor", "follower", motor);
            }

            CTREMotor slave = (CTREMotor) motor;
            slave.setHardware("follower", this.master.getDeviceNumber(), "none");
            slaves.add(slave);
        }
    }

    @Override
    public void processFlag(String flag) {
        master.processFlag(flag);
        for (CTREMotor slave : slaves) {
            slave.processFlag(flag);
        }
    }

    @Override
    public void setHardware(String outputType, double outputValue, String profile) {
        master.setHardware(outputType, outputValue, profile);

        // TODO this causes talons/victors to read from the SharedHardwareFactory every frame
        // Uncomment this to read position, velocity, or current on follower motors
        for (CTREMotor slave : slaves) {
            slave.setHardware("follower", master.getDeviceNumber(), "none");
        }
    }
}
