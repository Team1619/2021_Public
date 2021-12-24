package org.team1619.modelfactory;

import org.team1619.behavior.Drivetrain_Percent;
import org.team1619.behavior.Drivetrain_Zero;
import org.uacr.models.behavior.Behavior;
import org.uacr.models.exceptions.ConfigurationException;
import org.uacr.robot.AbstractModelFactory;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.shared.abstractions.ObjectsDirectory;
import org.uacr.shared.abstractions.OutputValues;
import org.uacr.shared.abstractions.RobotConfiguration;
import org.uacr.utilities.Config;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

public class ModelFactory_Behaviors extends AbstractModelFactory {

    private static final Logger logger = LogManager.getLogger(ModelFactory_Behaviors.class);

    private final InputValues sharedInputValues;
    private final OutputValues sharedOutputValues;
    private final RobotConfiguration robotConfiguration;

    public ModelFactory_Behaviors(InputValues inputValues, OutputValues outputValues, RobotConfiguration robotConfiguration, ObjectsDirectory objectsDirectory) {
        super(inputValues, outputValues, robotConfiguration, objectsDirectory);
        sharedInputValues = inputValues;
        sharedOutputValues = outputValues;
        this.robotConfiguration = robotConfiguration;
    }

    public Behavior createBehavior(String name, Config config) {
        logger.trace("Creating behavior '{}' of type '{}' with config '{}'", name, config.getType(), config.getData());

        switch (name) {
            // Drivetrain
            case "bh_drivetrain_percent":
                return new Drivetrain_Percent(sharedInputValues, sharedOutputValues, config, robotConfiguration);
            case "bh_drivetrain_zero":
                return new Drivetrain_Zero(sharedInputValues, sharedOutputValues, config, robotConfiguration);

            // State not found
            default:
                throw new ConfigurationException("Behavior " + name + " does not exist.");
        }
    }

}