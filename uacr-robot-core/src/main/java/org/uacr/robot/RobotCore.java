package org.uacr.robot;

import org.uacr.services.input.InputService;
import org.uacr.services.output.OutputService;
import org.uacr.services.states.StatesService;
import org.uacr.shared.abstractions.*;
import org.uacr.shared.concretions.*;
import org.uacr.utilities.Config;
import org.uacr.utilities.YamlConfigParser;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;
import org.uacr.utilities.services.ScheduledMultiService;
import org.uacr.utilities.services.Scheduler;
import org.uacr.utilities.services.Service;
import org.uacr.utilities.services.managers.AsyncServiceManager;
import org.uacr.utilities.services.managers.ServiceManager;

import java.util.List;

public abstract class RobotCore {

    private static final Logger logger = LogManager.getLogger(RobotCore.class);

    protected final FMS fms;
    protected final RobotConfiguration robotConfiguration;
    protected final InputValues inputValues;
    protected final OutputValues outputValues;
    protected final HardwareFactory hardwareFactory;
    protected final EventBus eventBus;
    protected final ObjectsDirectory objectsDirectory;
    protected final AbstractStateControls stateControls;
    protected final ServiceManager serviceManager;
    protected final AbstractModelFactory modelFactory;

    protected RobotCore() {
        YamlConfigParser parser = new YamlConfigParser();
        parser.load("general.yaml");

        Config loggerConfig = parser.getConfig("logger");
        if (loggerConfig.contains("log_level")) {
            LogManager.setLogLevel(loggerConfig.getEnum("log_level", LogManager.Level.class));
        }

        fms = new SharedFMS();
        robotConfiguration = new SharedRobotConfiguration();
        inputValues = new SharedInputValues();
        outputValues = new SharedOutputValues();
        hardwareFactory = new SharedHardwareFactory();
        eventBus = new SharedEventBus();
        objectsDirectory = new SharedObjectsDirectory();
        stateControls = createStateControls();

        modelFactory = createModelFactory();

        StatesService statesService = new StatesService(modelFactory, inputValues, fms, robotConfiguration,
                objectsDirectory, stateControls);

        InputService inputService = new InputService(modelFactory, inputValues, robotConfiguration,
                objectsDirectory);

        OutputService outputService = new OutputService(modelFactory, fms, inputValues,
                outputValues, robotConfiguration, objectsDirectory);

        serviceManager = new AsyncServiceManager(
                new ScheduledMultiService(new Scheduler(3), inputService, statesService, outputService),
                new ScheduledMultiService(new Scheduler(30), createInfoServices()));
    }

    protected abstract AbstractStateControls createStateControls();

    protected abstract AbstractModelFactory createModelFactory();

    protected abstract List<Service> createInfoServices();

    public void start() {
        logger.info("Starting services");
        serviceManager.start();
        serviceManager.awaitHealthy();
        logger.info("********************* ALL SERVICES STARTED *******************************");
    }

    public FMS getFms() {
        return fms;
    }
}
