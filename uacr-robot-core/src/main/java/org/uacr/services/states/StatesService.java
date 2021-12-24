package org.uacr.services.states;

import org.uacr.robot.AbstractModelFactory;
import org.uacr.robot.AbstractStateControls;
import org.uacr.robot.RobotManager;
import org.uacr.shared.abstractions.FMS;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.shared.abstractions.ObjectsDirectory;
import org.uacr.shared.abstractions.RobotConfiguration;
import org.uacr.utilities.Config;
import org.uacr.utilities.YamlConfigParser;
import org.uacr.utilities.injection.Inject;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;
import org.uacr.utilities.services.ScheduledService;
import org.uacr.utilities.services.Scheduler;

/**
 * Reads the FMS mode sent to us by the field and runs the correct StateControls and StateMachine
 */

public class StatesService implements ScheduledService {

    private static final Logger logger = LogManager.getLogger(StatesService.class);

    private final AbstractModelFactory modelFactory;
    private final InputValues sharedInputValues;
    private final ObjectsDirectory sharedObjectsDirectory;
    private final FMS fms;
    private final YamlConfigParser statesParser;
    private final RobotConfiguration robotConfiguration;
    private final StateMachine stateMachine;
    private final RobotManager robotManager;

    private FMS.Mode currentFmsMode;
    private long frameTimeThreshold;

    /**
     * @param inputValues the map that holds the values from all the inputs
     * @param fms holds the  value of the current FMS mode (Auto, Teleop)
     * @param robotConfiguration passed into RobotManager and StateMachine as well as used to read in general config values
     * @param objectsDirectory objectsDirectory used to store the state objects
     * @param stateControls used to control the state of the robot such as modes controlled by the drivers (passed into the robot manager)
     */

    @Inject
    public StatesService(AbstractModelFactory modelFactory, InputValues inputValues, FMS fms, RobotConfiguration robotConfiguration, ObjectsDirectory objectsDirectory, AbstractStateControls stateControls) {
        this.modelFactory = modelFactory;
        sharedInputValues = inputValues;
        sharedObjectsDirectory = objectsDirectory;
        this.fms = fms;
        statesParser = new YamlConfigParser();
        this.robotConfiguration = robotConfiguration;
        robotManager = new RobotManager(sharedInputValues, robotConfiguration, stateControls);
        stateMachine = new StateMachine(sharedObjectsDirectory, robotManager, robotConfiguration, sharedInputValues);

        currentFmsMode = fms.getMode();
        frameTimeThreshold = -1;
    }

    /**
     * Called when the code is stated up by the abstractSchedulerService
     *
     * @throws Exception if it does not start up correctly
     */
    @Override
    public void startUp() throws Exception {
        logger.trace("Starting StatesService");

        frameTimeThreshold = robotConfiguration.getInt("global_timing", "frame_time_threshold_state_service");

        statesParser.loadWithFolderName("states.yaml");
        createAllStates(statesParser);

        sharedInputValues.setBoolean("ipb_robot_has_been_zeroed", false);

        logger.trace("StatesService started");
    }

    /**
     * Determines the frame rate for this service
     *
     * @return the frame rate
     */
    @Override
    public Scheduler scheduler() {
        return new Scheduler(1000 / 60);
    }

    /**
     * Called every frame by the abstractSchedulerService based on the frame time set in scheduler() above
     * Decides what mode we are running (Auto, Teleop, Disabled)
     * Updates the instance of StateControls and the StateMachine
     */
    @Override
    public void runOneIteration() throws Exception {

        double frameStartTime = System.currentTimeMillis();

        //Get the FMS mode from the field or webDashboard
        FMS.Mode nextFmsMode = fms.getMode();

        //If mode is changing
        if (nextFmsMode != currentFmsMode) {

            // Initialize StateMachine with either Teleop or Auto StateControls
            if (nextFmsMode == FMS.Mode.AUTONOMOUS) {
                sharedInputValues.setBoolean("ipb_robot_has_been_zeroed", false);
                robotManager.initialize(nextFmsMode);
                stateMachine.initialize();
            } else if (nextFmsMode == FMS.Mode.TELEOP) {
                if(!sharedInputValues.getBoolean("ipb_robot_has_been_zeroed") || sharedInputValues.getBoolean("ipb_auto_complete")) {
                    sharedInputValues.setBoolean("ipb_auto_complete", false);
                    robotManager.dispose();
                    stateMachine.dispose();
                    robotManager.initialize(nextFmsMode);
                    stateMachine.initialize();
                }
            } else if (nextFmsMode == FMS.Mode.DISABLED) {
                logger.info("Current mode {} next mode {}", currentFmsMode, nextFmsMode);
  //              if (currentFmsMode != FMS.Mode.AUTONOMOUS) {
                    robotManager.dispose();
                    stateMachine.dispose();
                    sharedInputValues.setBoolean("ipb_robot_has_been_zeroed", false);
  //              }
            }
        }

        //Update the current RobotManger and update the StateMachine
        currentFmsMode = nextFmsMode;
        if (currentFmsMode == FMS.Mode.DISABLED) {
            robotManager.disabledUpdate();
        }
        else {
            robotManager.update();
            stateMachine.update();
        }

        // Check for delayed frames
        double currentTime = System.currentTimeMillis();
        double frameTime = currentTime - frameStartTime;
        sharedInputValues.setNumeric("ipn_frame_time_states_service", frameTime);
        if (frameTime > frameTimeThreshold) {
            logger.debug("********** States Service frame time = {}", frameTime);
        }
    }

    /**
     * Shuts down the StateService
     * @throws Exception
     */

    @Override
    public void shutDown() throws Exception {

    }


    /**
     * Loops through all states and calls the method to create them and store them
     * @param statesParser holds the information from the States yaml file
     */

    private void createAllStates(YamlConfigParser statesParser) {
        for (String stateName : robotConfiguration.getStateNames()) {
            Config config = statesParser.getConfig(stateName);
            createState(stateName, statesParser, config);
        }
    }

    /**
     * Uses the ModelFactory to create the desired state
     * @param name of the state to be created
     * @param statesParser holds the information from the States yaml file
     * @param config for the state
     */

    private void createState(String name, YamlConfigParser statesParser, Config config) {
        modelFactory.createState(name, statesParser, config);
    }
}
