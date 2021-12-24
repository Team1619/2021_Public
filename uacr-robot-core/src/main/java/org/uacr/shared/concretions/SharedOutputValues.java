package org.uacr.shared.concretions;

import org.uacr.shared.abstractions.OutputValues;
import org.uacr.utilities.Maps;
import org.uacr.utilities.injection.Singleton;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores the values to be set to all the outputs
 * Shared between most classes so values can be assigned
 */

@Singleton
public class SharedOutputValues implements OutputValues {

    private static final Logger logger = LogManager.getLogger(SharedOutputValues.class);

    private final Map<String, Map<String, Object>> outputNumerics;
    private final Map<String, Boolean> outputBooleans;
    private final Map<String, Set<String>> outputFlags;

    /**
     * Creates maps to store the values to be set to each output by output type and value type
     */

    public SharedOutputValues() {
        outputNumerics = new ConcurrentHashMap<String, Map<String, Object>>();
        outputBooleans = new ConcurrentHashMap<String, Boolean>();
        outputFlags = new ConcurrentHashMap<String, Set<String>>();
    }

    /**
     * @return a map containing all the outputs (numeric and boolean) and their values
     */

    // All
    @Override
    public Map<String, Object> getAllOutputs() {
        Map<String, Object> allOutputs = new HashMap<>();

        for (HashMap.Entry<String, Map<String, Object>> outputNumeric : outputNumerics.entrySet()) {
            allOutputs.put(outputNumeric.getKey(), outputNumeric.getValue().get("value"));
        }
        allOutputs.putAll(outputBooleans);

        return allOutputs;
    }

    /**
     * Output flags are used to pass extra information to the outputs
     * Output flags are cleared after one reading
     * @param name of the output to set the flag on
     * @param flag the value to be set
     */

    @Override
    public void setOutputFlag(String name, String flag) {


        if (!outputFlags.containsKey(name)) {
            outputFlags.put(name, new HashSet<String>());
        }
        outputFlags.get(name).add(flag);
    }

    /**
     * Output flags are used to pass extra information to the outputs
     * @param name of the output read the flag on
     * @return the value of the flag after removing the flag from the map
     */
    @Override
    public Set<String> getOutputFlags(String name) {
        Set<String> flags = outputFlags.getOrDefault(name, Collections.emptySet());
        outputFlags.remove(name);
        return flags;
    }


    /**
     * Updates the value of an OutputNumeric
     * Does not accept a profile string
     * @param outputNumericName name of the OutputNumeric to be updated
     * @param outputType type of output such as "percent" mode for a motor
     * @param outputValue the value to be set
     */
    // Numeric
    @Override
    public void setNumeric(String outputNumericName, String outputType, double outputValue) {
        setNumeric(outputNumericName, outputType, outputValue, "none");
    }

    /**
     * Updates the value of an OutputNumeric
     * Does accept a profile string
     * @param outputNumericName name of the OutputNumeric to be updated
     * @param outputType type of output such as "percent" mode for a motor
     * @param outputValue the value to be set
     * @param profile a string used to identify the profile used to control the output such as the PID profile for a motor
     */

    @Override
    public void setNumeric(String outputNumericName, String outputType, double outputValue, String profile) {
        outputNumerics.put(outputNumericName, Maps.of("type", outputType, "value", outputValue, "profile", profile));
    }

    /**
     * @param outputNumericName name of the OutputNumeric to be read
     * @return a map containing all of the values of the OutputNumeric
     */

    @Override
    public Map<String, Object> getOutputNumericValue(String outputNumericName) {
        return outputNumerics.getOrDefault(outputNumericName, Maps.of("value", 0.0, "type", "percent", "profile", "none"));
    }

    /**
     * @param outputBooleanName the name of the OutputBoolean to have its value updated
     * @param outputValue the value to update the OutputBoolean to
     */

    // Boolean
    @Override
    public void setBoolean(String outputBooleanName, boolean outputValue) {
        outputBooleans.put(outputBooleanName, outputValue);
    }

    /**
     * @param outputBooleanName name of the OutputBoolean to be read
     * @return the value of the OutputBoolean
     */
    @Override
    public boolean getBoolean(String outputBooleanName) {
        return outputBooleans.getOrDefault(outputBooleanName, false);
    }
}
