package org.team1619.modelfactory;

import org.team1619.robot.AbstractRobotModelFactory;
import org.uacr.robot.AbstractModelFactory;
import org.uacr.shared.abstractions.*;
import org.uacr.utilities.injection.Inject;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

public class RobotModelFactory extends AbstractModelFactory {

    private static final Logger logger = LogManager.getLogger(RobotModelFactory.class);

    @Inject
    public RobotModelFactory(HardwareFactory hardwareFactory, InputValues inputValues, OutputValues outputValues, RobotConfiguration robotConfiguration, ObjectsDirectory objectsDirectory) {
        super(inputValues, outputValues, robotConfiguration, objectsDirectory);
        registerModelFactory(new AbstractRobotModelFactory(hardwareFactory, inputValues, outputValues, robotConfiguration, objectsDirectory));
        registerModelFactory(new ModelFactory_Behaviors(inputValues, outputValues, robotConfiguration, objectsDirectory));
    }
}