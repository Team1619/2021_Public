package org.uacr.services.output;

import org.uacr.models.outputs.bool.OutputBoolean;
import org.uacr.models.outputs.numeric.OutputNumeric;
import org.uacr.robot.AbstractModelFactory;
import org.uacr.shared.abstractions.*;
import org.uacr.utilities.Config;
import org.uacr.utilities.YamlConfigParser;
import org.uacr.utilities.injection.Inject;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;
import org.uacr.utilities.services.ScheduledService;
import org.uacr.utilities.services.Scheduler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Initializes and updates all outputs using values from SharedOutputValues
 */

public class OutputService implements ScheduledService {

    private static final Logger logger = LogManager.getLogger(OutputService.class);

    private final AbstractModelFactory modelFactory;
    private final FMS fms;
    private final InputValues sharedInputValues;
    private final OutputValues sharedOutputValues;
    private final ObjectsDirectory sharedOutputsDirectory;
    private final RobotConfiguration robotConfiguration;
    private final YamlConfigParser outputBooleansParser;
    private final YamlConfigParser outputNumericsParser;

    private FMS.Mode currentFmsMode;
    private Set<String> outputBooleanNames;
    private Set<String> outputNumericNames;
    private long frameTimeThreshold;

    /**
     * @param modelFactory the ModelFactory to be used
     * @param inputValues the map that holds the values from all the inputs
     * @param outputValues the map that holds the values from all the outputs
     * @param robotConfiguration used to obtain a list of all the outputs to be created as well as other configuration information used by the OutputService
     * @param objectsDirectory objectsDirectory used to store the output objects
     */

    @Inject
    public OutputService(AbstractModelFactory modelFactory, FMS fms, InputValues inputValues, OutputValues outputValues, RobotConfiguration robotConfiguration, ObjectsDirectory objectsDirectory) {
        this.modelFactory = modelFactory;
        this.fms = fms;
        sharedInputValues = inputValues;
        sharedOutputValues = outputValues;
        this.robotConfiguration = robotConfiguration;
        sharedOutputsDirectory = objectsDirectory;
        outputBooleansParser = new YamlConfigParser();
        outputNumericsParser = new YamlConfigParser();

        currentFmsMode = FMS.Mode.DISABLED;
        outputNumericNames = new HashSet<>();
        outputBooleanNames = new HashSet<>();
        frameTimeThreshold = -1;
    }


    /**
     * Starts the OutputService
     * Obtains a list of all output objects (boolean and numeric)
     * Loads the output yaml files
     * Registers the outputs with the objects directory (this creates them)
     * @throws Exception if the start up process does no succeed
     */

    @Override
    public void startUp() throws Exception {
        logger.trace("Starting OutputService");

        outputNumericNames = robotConfiguration.getOutputNumericNames();
        outputBooleanNames = robotConfiguration.getOutputBooleanNames();
        frameTimeThreshold = robotConfiguration.getInt("global_timing", "frame_time_threshold_output_service");

        outputBooleansParser.loadWithFolderName("output-booleans.yaml");
        outputNumericsParser.loadWithFolderName("output-numerics.yaml");
        createAllOutputs(outputBooleansParser, outputNumericsParser);

        logger.trace("OutputService started");
    }

    /**
     * Runs every frame
     * Loops through all outputs and updates output values using the values in the sharedOutputValues map
     * Monitors for long frame times
     * @throws Exception if it does not run cleanly
     */


    @Override
    public void runOneIteration() throws Exception {

        long frameStartTime = System.currentTimeMillis();

        FMS.Mode nextFmsMode = fms.getMode();

        if (currentFmsMode == FMS.Mode.DISABLED && nextFmsMode != FMS.Mode.DISABLED) {
            for (String outputNumericName : outputNumericNames) {
                OutputNumeric outputNumericObject = sharedOutputsDirectory.getOutputNumericObject(outputNumericName);
                outputNumericObject.initialize();
            }
            for (String outputBooleanName : outputBooleanNames) {
                OutputBoolean outputBooleanObject = sharedOutputsDirectory.getOutputBooleanObject(outputBooleanName);
                outputBooleanObject.initialize();
            }
        }

        currentFmsMode = nextFmsMode;

        for (String name : outputNumericNames) {
            OutputNumeric outputNumericObject = sharedOutputsDirectory.getOutputNumericObject(name);
            Map<String, Object> outputNumericOutputs = sharedOutputValues.getOutputNumericValue(name);
            for(String flag : sharedOutputValues.getOutputFlags(name)) {
                outputNumericObject.processFlag(flag);
            }
            outputNumericObject.setHardware((String) outputNumericOutputs.get("type"), (double) outputNumericOutputs.get("value"), (String) outputNumericOutputs.get("profile"));
        }
        for (String name : outputBooleanNames) {
            OutputBoolean outputBooleanObject = sharedOutputsDirectory.getOutputBooleanObject(name);
            for(String flag : sharedOutputValues.getOutputFlags(name)) {
                outputBooleanObject.processFlag(flag);
            }
            outputBooleanObject.setHardware(sharedOutputValues.getBoolean(name));
        }

        // Check for delayed frames
        long currentTime = System.currentTimeMillis();
        long frameTime = currentTime - frameStartTime;
        sharedInputValues.setNumeric("ipn_frame_time_output_service", frameTime);
        if (frameTime > frameTimeThreshold) {
            logger.debug("********** Output Service frame time = {}", frameTime);
        }
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
     * Loops through all outputs and calls the appropriate method to create them and store them in the appropriate map
     * @param outputsBooleanParser holds the information from the OutputBooleans yaml file
     * @param outputNumericsParser holds the information from the OutputNumerics yaml file
     */

    private void createAllOutputs(YamlConfigParser outputsBooleanParser, YamlConfigParser outputNumericsParser) {
        for (String outputBooleanName : robotConfiguration.getOutputBooleanNames()) {
            Config outputBooleanConfig = outputsBooleanParser.getConfig(outputBooleanName);
            createOutputBoolean(outputBooleanName, outputBooleanConfig, outputsBooleanParser);
            logger.trace("Registered {} in Output Booleans", outputBooleanName);
        }

        for (String outputNumericName : robotConfiguration.getOutputNumericNames()) {
            Config outputNumericConfig = outputNumericsParser.getConfig(outputNumericName);
            createOutputNumeric(outputNumericName, outputNumericConfig, outputNumericsParser);
            logger.trace("Registered {} in Output Numerics", outputNumericName);
        }
    }

    /**
     * Uses the ModelFactory to create the desired OutputBoolean and stores it in the OutputBooleanObjects map
     * @param name of the output to be created
     * @param config the yaml configuration for the output
     */

    private void createOutputBoolean(String name, Config config, YamlConfigParser parser) {
        sharedOutputsDirectory.registerOutputBoolean(name, modelFactory.createOutputBoolean(name, config, parser));
    }

    /**
     * Uses the ModelFactory to create the desired OutputNumeric and stores it in the OutputNumericObjects map
     * @param name of the output to be created
     * @param config the yaml configuration for the output
     */

    private void createOutputNumeric(String name, Config config, YamlConfigParser parser) {
        sharedOutputsDirectory.registerOutputNumeric(name, modelFactory.createOutputNumeric(name, config, parser));
    }
}
