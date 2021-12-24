package org.uacr.shared.concretions;

import org.uacr.models.behavior.Behavior;
import org.uacr.models.inputs.bool.InputBoolean;
import org.uacr.models.inputs.numeric.InputNumeric;
import org.uacr.models.inputs.vector.InputVector;
import org.uacr.models.outputs.bool.OutputBoolean;
import org.uacr.models.outputs.numeric.OutputNumeric;
import org.uacr.models.state.State;
import org.uacr.shared.abstractions.ObjectsDirectory;
import org.uacr.utilities.injection.Inject;
import org.uacr.utilities.injection.Singleton;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles the creation and use of objects (inputs, states, outputs, behaviors)
 * Does not handle hardware objects
 */

@Singleton
public class SharedObjectsDirectory implements ObjectsDirectory {

    private static final Logger logger = LogManager.getLogger(SharedObjectsDirectory.class);

    private final Map<String, InputBoolean> inputBooleanObjects;
    private final Map<String, InputNumeric> inputNumericObjects;
    private final Map<String, InputVector> inputVectorObjects;
    private final Map<String, OutputNumeric> outputNumericObjects;
    private final Map<String, OutputBoolean> outputBooleanObjects;
    private final Map<Object, State> stateObjects;
    private final Map<Object, Behavior> behaviorObjects;

    @Inject
    public SharedObjectsDirectory() {
        inputBooleanObjects = new ConcurrentHashMap<>();
        inputNumericObjects = new ConcurrentHashMap<>();
        inputVectorObjects = new ConcurrentHashMap<>();
        outputNumericObjects = new ConcurrentHashMap<>();
        outputBooleanObjects = new ConcurrentHashMap<>();
        stateObjects = new ConcurrentHashMap<>();
        behaviorObjects = new ConcurrentHashMap<>();
    }

    //--------------------------- Inputs ----------------------------------------//


    @Override
    public void registerInputBoolean(String name, InputBoolean inputBoolean) {
        inputBooleanObjects.put(name, inputBoolean);
    }

    @Override
    public void registerInputNumeric(String name, InputNumeric inputNumeric) {
        inputNumericObjects.put(name, inputNumeric);
    }

    @Override
    public void registerInputVector(String name, InputVector inputVector) {
        inputVectorObjects.put(name, inputVector);
    }

    @Override
    public InputBoolean getInputBooleanObject(String name) {
        return inputBooleanObjects.get(name);
    }

    @Override
    public InputNumeric getInputNumericObject(String name) {
        return inputNumericObjects.get(name);
    }

    @Override
    public InputVector getInputVectorObject(String name) {
        return inputVectorObjects.get(name);
    }

    //--------------------------- Behaviors ----------------------------------------//

    /**
     * Adds a behavior object to the BehaviorObjects map
     * @param name of the behavior object to be added
     * @param behavior the behavior object
     */
    @Override
    public void setBehaviorObject(String name, Behavior behavior) {
        behaviorObjects.put(name, behavior);
    }

    /**
     * @param name of the desired behavior object
     * @return the behavior object
     */

    @Override
    @Nullable
    public Behavior getBehaviorObject(String name) {
        return behaviorObjects.get(name);
    }

    //--------------------------- States ----------------------------------------//

    /**
     * Adds a state to the StateObjects map
     * @param name of state to be added
     * @param state the state object
     */

    @Override
    public void registerStateObject(String name, State state) {
        stateObjects.put(name, state);
    }

    /**
     * @param name of desired state
     * @return the state object
     */

    @Override
    public State getStateObject(String name) {
        return stateObjects.get(name);
    }

    //--------------------------- Outputs ----------------------------------------//

    @Override
    public void registerOutputBoolean(String name, OutputBoolean outputBoolean) {
        outputBooleanObjects.put(name, outputBoolean);
    }

    @Override
    public void registerOutputNumeric(String name, OutputNumeric outputNumeric) {
        outputNumericObjects.put(name, outputNumeric);
    }

    @Override
    public OutputBoolean getOutputBooleanObject(String outputBooleanName) {
        return outputBooleanObjects.get(outputBooleanName);
    }

    @Override
    public OutputNumeric getOutputNumericObject(String outputNumericName) {
        return outputNumericObjects.get(outputNumericName);
    }
}
