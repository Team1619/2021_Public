package org.uacr.services.input;

import org.uacr.models.inputs.bool.InputBoolean;
import org.uacr.models.inputs.numeric.InputNumeric;
import org.uacr.models.inputs.vector.InputVector;
import org.uacr.robot.AbstractModelFactory;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.shared.abstractions.ObjectsDirectory;
import org.uacr.shared.abstractions.RobotConfiguration;
import org.uacr.utilities.Config;
import org.uacr.utilities.RobotSystem;
import org.uacr.utilities.YamlConfigParser;
import org.uacr.utilities.injection.Inject;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;
import org.uacr.utilities.services.ScheduledService;
import org.uacr.utilities.services.Scheduler;

import java.util.HashSet;
import java.util.Set;

/**
 * Initializes and updates all inputs using values from SharedInputValues
 */

public class InputService implements ScheduledService {

    private static final Logger logger = LogManager.getLogger(InputService.class);

    private final AbstractModelFactory modelFactory;
    private final InputValues sharedInputValues;
    private final ObjectsDirectory sharedObjectsDirectory;
    private final RobotConfiguration robotConfiguration;
    private final YamlConfigParser inputBooleanParser;
    private final YamlConfigParser inputNumericParser;
    private final YamlConfigParser inputVectorParser;

    private Set<String> inputBooleanNames;
    private Set<String> inputNumericNames;
    private Set<String> inputVectorNames;
    private long previousTime;
    private long frameTimeThreshold;
    private long frameCycleTimeThreshold;

    /**
     * @param modelFactory the ModelFactory to be used
     * @param inputValues the map that holds the values from all the inputs
     * @param robotConfiguration used to obtain a list of all the inputs to be created as well as other configuration information used by the InputService
     * @param objectsDirectory used to store the input objects
     */

    @Inject
    public InputService(AbstractModelFactory modelFactory, InputValues inputValues, RobotConfiguration robotConfiguration, ObjectsDirectory objectsDirectory) {
        this.modelFactory = modelFactory;
        sharedInputValues = inputValues;
        sharedObjectsDirectory = objectsDirectory;
        this.robotConfiguration = robotConfiguration;
        inputBooleanParser = new YamlConfigParser();
        inputNumericParser = new YamlConfigParser();
        inputVectorParser = new YamlConfigParser();

        inputBooleanNames = new HashSet<>();
        inputNumericNames = new HashSet<>();
        inputVectorNames = new HashSet<>();
        previousTime = -1;
        frameTimeThreshold = -1;
        frameCycleTimeThreshold = -1;
    }

    /**
     * Starts the input service
     * Obtains a list of all input objects (boolean, numeric and vector)
     * Loads the input yaml files
     * Registers the inputs with the objects directory (this creates them)
     * @throws Exception if the start up process does no succeed
     */

    @Override
    public void startUp() throws Exception {
        logger.trace("Starting InputService");

        inputBooleanNames = robotConfiguration.getInputBooleanNames();
        inputNumericNames = robotConfiguration.getInputNumericNames();
        inputVectorNames = robotConfiguration.getInputVectorNames();
        previousTime = System.currentTimeMillis();
        frameTimeThreshold = robotConfiguration.getInt("global_timing", "frame_time_threshold_input_service");
        frameCycleTimeThreshold = robotConfiguration.getInt("global_timing", "frame_cycle_time_threshold_core_thread");

        inputBooleanParser.loadWithFolderName("input-booleans.yaml");
        inputNumericParser.loadWithFolderName("input-numerics.yaml");
        inputVectorParser.loadWithFolderName("input-vectors.yaml");
        createAllInputs(inputBooleanParser, inputNumericParser, inputVectorParser);

        sharedInputValues.setString("active states", "");

        logger.trace("InputService started");
    }

    /**
     * Runs every frame
     * Loops through all inputs and updates their values in SharedInputValues
     * Monitors for long frame times
     * @throws Exception if it does not run cleanly
     */

    @Override
    public void runOneIteration() throws Exception {

        RobotSystem.update();

        long frameStartTime = System.currentTimeMillis();
        sharedInputValues.setNumeric("ipn_frame_start_time", frameStartTime);

        for (String name : inputBooleanNames) {
            InputBoolean inputBoolean = sharedObjectsDirectory.getInputBooleanObject(name);
            for(String flag : sharedInputValues.getInputFlags(name)){
                inputBoolean.processFlag(flag);
            }
            inputBoolean.update();
            sharedInputValues.setBoolean(name, inputBoolean.get());
            switch (inputBoolean.getDelta()) {
                case RISING_EDGE:
                    sharedInputValues.setBooleanRisingEdge(name, true);
                    // Comment out to use single use rising edge
                    sharedInputValues.setBooleanFallingEdge(name, false);
                    break;
                case FALLING_EDGE:
                    sharedInputValues.setBooleanFallingEdge(name, true);
                    // Comment out to use single use falling edge
                    sharedInputValues.setBooleanRisingEdge(name, false);
                    break;
                default:
                    // Comment out to use single use falling edge
                    sharedInputValues.setBooleanFallingEdge(name, false);
                    // Comment out to use single use rising edge
                    sharedInputValues.setBooleanRisingEdge(name, false);
                    break;
            }
        }

        //logger.trace("Updated boolean inputs");

        for (String name : inputNumericNames) {
            InputNumeric inputNumeric = sharedObjectsDirectory.getInputNumericObject(name);
            for (String flag : sharedInputValues.getInputFlags(name)){
                inputNumeric.processFlag(flag);
            }
            inputNumeric.update();
            sharedInputValues.setNumeric(name, inputNumeric.get());
        }

        //logger.trace("Updated numeric inputs");

        for (String name : inputVectorNames) {
            InputVector inputVector = sharedObjectsDirectory.getInputVectorObject(name);
            for (String flag : sharedInputValues.getInputFlags(name)){
                inputVector.processFlag(flag);
            }
            inputVector.update();
            sharedInputValues.setVector(name, inputVector.get());
        }

        //logger.trace("Updated vector inputs");

        // Check for delayed frames
        long currentTime = System.currentTimeMillis();
        long frameTime = currentTime - frameStartTime;
        long totalCycleTime = frameStartTime - previousTime;
        sharedInputValues.setNumeric("ipn_frame_time_input_service", frameTime);
        sharedInputValues.setNumeric("ipn_frame_cycle_time_core_thread", totalCycleTime);
        if (frameTime > frameTimeThreshold) {
            logger.debug("********** Input Service frame time = {}", frameTime);
        }
        if (totalCycleTime > frameCycleTimeThreshold) {
            logger.debug("********** Core thread frame cycle time = {}", totalCycleTime);
        }
        previousTime = frameStartTime;
    }

    /**
     * Shuts down the InputService
     * Currently preforms no actions
     * @throws Exception
     */

    @Override
    public void shutDown() throws Exception {

    }

    /**
     * @return a new Scheduler class with the desired frame duration
     */

    @Override
    public Scheduler scheduler() {
        return new Scheduler(1000 / 60);
    }

    /**
     * Loops through all inputs and calls the appropriate method to create them and store them in the appropriate map
     * Once all inputs have been created, calls initialize on all inputs
     * @param inputBooleansParser holds the information from the InputBooleans yaml file
     * @param inputNumericsParser holds the information from the InputNumerics yaml file
     * @param inputVectorsParser holds the information from the InputVectors yaml file
     */

    private void createAllInputs(YamlConfigParser inputBooleansParser, YamlConfigParser inputNumericsParser, YamlConfigParser inputVectorsParser) {
        Set<String> allInputBooleanNames = robotConfiguration.getInputBooleanNames();
        Set<String> allInputNumericNames = robotConfiguration.getInputNumericNames();
        Set<String> allInputVectorNames = robotConfiguration.getInputVectorNames();

        for (String inputBooleanName : allInputBooleanNames) {
            Config config = inputBooleansParser.getConfig(inputBooleanName);
            createInputBoolean(inputBooleanName, config);
        }

        for (String inputNumericName : allInputNumericNames) {
            Config config = inputNumericsParser.getConfig(inputNumericName);
            createInputNumeric(inputNumericName, config);
        }

        for (String inputVectorName : allInputVectorNames) {
            Config config = inputVectorsParser.getConfig(inputVectorName);
            createInputVector(inputVectorName, config);
        }

        allInputBooleanNames.stream().map(sharedObjectsDirectory::getInputBooleanObject).forEach(InputBoolean::initialize);
        logger.trace("Input Booleans initialized");

        allInputNumericNames.stream().map(sharedObjectsDirectory::getInputNumericObject).forEach(InputNumeric::initialize);
        logger.trace("Input Numerics initialized");

        allInputVectorNames.stream().map(sharedObjectsDirectory::getInputVectorObject).forEach(InputVector::initialize);
        logger.trace("Input Vectors initialized");

    }

    /**
     * Uses the ModelFactory to create the desired InputBoolean and stores it in the ObjectsDirectory
     * @param name of the input to be created
     * @param config the yaml configuration for the input
     */

    private void createInputBoolean(String name, Config config) {
        sharedObjectsDirectory.registerInputBoolean(name, modelFactory.createInputBoolean(name, config));
    }

    /**
     * Uses the ModelFactory to create the desired InputNumeric and stores it in the ObjectsDirectory
     * @param name of the input to be created
     * @param config the yaml configuration for the input
     */

    private void createInputNumeric(String name, Config config) {
        sharedObjectsDirectory.registerInputNumeric(name, modelFactory.createInputNumeric(name, config));
    }

    /**
     * Uses the ModelFactory to create the desired InputVector and stores it in the ObjectsDirectory
     * @param name of the input to be created
     * @param config the yaml configuration for the input
     */

    private void createInputVector(String name, Config config) {
        sharedObjectsDirectory.registerInputVector(name, modelFactory.createInputVector(name, config));
    }
}
