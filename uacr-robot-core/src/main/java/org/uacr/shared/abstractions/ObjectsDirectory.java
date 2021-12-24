package org.uacr.shared.abstractions;

import org.uacr.models.behavior.Behavior;
import org.uacr.models.inputs.bool.InputBoolean;
import org.uacr.models.inputs.numeric.InputNumeric;
import org.uacr.models.inputs.vector.InputVector;
import org.uacr.models.outputs.bool.OutputBoolean;
import org.uacr.models.outputs.numeric.OutputNumeric;
import org.uacr.models.state.State;
import org.uacr.utilities.Config;
import org.uacr.utilities.YamlConfigParser;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

/**
 * Handles the creation (using the ModelFactory) of the input, output, state and behavior objects
 */

public interface ObjectsDirectory {

    // Inputs
    void registerInputBoolean(String name, InputBoolean inputBoolean);

    void registerInputNumeric(String name, InputNumeric inputNumeric);

    void registerInputVector(String name, InputVector inputVector);

    InputBoolean getInputBooleanObject(String name);

    InputNumeric getInputNumericObject(String name);

    InputVector getInputVectorObject(String name);


    // Behaviors
    void setBehaviorObject(String name, Behavior behavior);

    @Nullable
    Behavior getBehaviorObject(String name);


    // States
    void registerStateObject(String name, State state);

    State getStateObject(String name);


    // Outputs
    void registerOutputBoolean(String name, OutputBoolean outputBoolean);

    void registerOutputNumeric(String name, OutputNumeric outputNumeric);

    OutputBoolean getOutputBooleanObject(String name);

    OutputNumeric getOutputNumericObject(String name);
}
