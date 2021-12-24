package org.uacr.shared.concretions;

import org.uacr.models.exceptions.ConfigurationException;
import org.uacr.models.exceptions.ConfigurationInvalidTypeException;
import org.uacr.shared.abstractions.RobotConfiguration;
import org.uacr.utilities.YamlConfigParser;
import org.uacr.utilities.injection.Singleton;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Stores and allows access to all information in the robot-configuration.yaml file
 */

@Singleton
public class SharedRobotConfiguration implements RobotConfiguration {

    private static final Logger logger = LogManager.getLogger(SharedRobotConfiguration.class);

    private Map<String, Map<String, Object>> data;

    public SharedRobotConfiguration() {
        logger.trace("Loading robot-configuration.yaml file");

        YamlConfigParser parser = new YamlConfigParser();
        parser.loadWithFolderName("robot-configuration.yaml");
        data = parser.getData();

        logger.trace("Loaded");
    }

    /**
     * load robot-configuration.yaml file
     */

    @Override
    public void initialize() {
        // no longer used
    }

    /**
     * @return a map containing all the state names listed in robot-configuration.yaml organized by priority level only
     */

    @Override
    public Map<String, Set<String>> getStateNamesWithPriority() {
        // Holds keys for all the subsystems plus sequences and parallels
        Set<String> stateKeys = new LinkedHashSet<>();

        stateKeys.add("sequences");
        stateKeys.add("parallels");

        stateKeys.addAll(getSubsystemNames());

        // A map of all states listed in robot-configuration.yaml
        @Nullable
        Map<String, Map<String, List<String>>> yamlStateMaps = getMap("general", "states");

        // Holds the states sorted by priority and not stateKey (subsystem name, sequence or parallel)
        Map<String, Set<String>> stateMap = new HashMap<>();

        // Loop through each state key (subsystem name, sequence or parallel) and add its states to the stateMap with the correct priority level
        if (yamlStateMaps != null) {
            for (String stateKey : stateKeys) {
                if (!yamlStateMaps.containsKey(stateKey)) {
                    continue;
                }

                @Nullable
                Map<String, List<String>> singleKeyStateMap = yamlStateMaps.get(stateKey);

                // Loops through each priority level for a single state key (subsystem name, sequence or parallel)
                if (singleKeyStateMap != null) {
                    for (Map.Entry<String, List<String>> singlePriorityMap : singleKeyStateMap.entrySet()) {
                        String priority = singlePriorityMap.getKey();
                        // Removes unnecessary text to reduce priory levels to ints
                        priority = priority.replace("priority_level_", "");
                        // A list of all the states in the current priority level for this stateKey (subsystem name, sequence or parallel)
                        List<String> singlePriorityStateList = singlePriorityMap.getValue();
                        //If the current priority level already exists add all states for this stateKey (subsystem name, sequence or parallel)
                        //If it doesn't already exist, create it and then add all states for this stateKey (subsystem name, sequence or parallel)
                        if (stateMap.containsKey(priority)) {
                            stateMap.get(priority).addAll(singlePriorityStateList);
                        } else {
                            Set<String> singlePriorityStateNames = new LinkedHashSet<>();

                            singlePriorityStateNames.addAll(singlePriorityStateList);

                            stateMap.put(priority, singlePriorityStateNames);
                        }
                    }
                }
            }
        }

        // A map containing all states grouped by priority level
        return stateMap;
    }

    /**
     * @return a set of all the state names listed in robot-configuration.yaml
     */

    @Override
    public Set<String> getStateNames() {
        Set<String> stateKeys = new LinkedHashSet<>();

        stateKeys.add("sequences");
        stateKeys.add("parallels");

        stateKeys.addAll(getSubsystemNames());

        @Nullable
        Map<String, Map<String, List<String>>> yamlStateMaps = getMap("general", "states");

        Set<String> stateSet = new LinkedHashSet<>();

        if (yamlStateMaps != null) {
            for (String stateKey : stateKeys) {
                if (!yamlStateMaps.containsKey(stateKey)) {
                    continue;
                }

                @Nullable
                Map<String, List<String>> singleKeyStateMap = yamlStateMaps.get(stateKey);

                if (singleKeyStateMap != null) {
                    for (Map.Entry<String, List<String>> singlePriorityMap : singleKeyStateMap.entrySet()) {
                        List<String> singlePriorityStateList = singlePriorityMap.getValue();

                        stateSet.addAll(singlePriorityStateList);
                    }
                }
            }
        }

        return stateSet;
    }

    /**
     * @return the list of subsystem names specified in robot-configuration.yaml
     */
    @Override
    public Set<String> getSubsystemNames() {
        return getSet("general", "subsystems");
    }

    /**
     * @return the list of InputBoolean names specified in robot-configuration.yaml
     */

    @Override
    public Set<String> getInputBooleanNames() {
        return getSet("general", "input_booleans");
    }

    /**
     * @return the list of InputNumeric names specified in robot-configuration.yaml
     */

    @Override
    public Set<String> getInputNumericNames() {
        return getSet("general", "input_numerics");
    }

    /**
     * @return the list of InputVector names specified in robot-configuration.yaml
     */

    @Override
    public Set<String> getInputVectorNames() {
        return getSet("general", "input_vectors");
    }

    /**
     * @return the list of OutputNumeric names specified in robot-configuration.yaml
     */

    @Override
    public Set<String> getOutputNumericNames() {
        return getSet("general", "output_numerics");
    }

    /**
     * @return the list of OutputBoolean names specified in robot-configuration.yaml
     */

    @Override
    public Set<String> getOutputBooleanNames() {
        return getSet("general", "output_booleans");
    }

    /**
     * Retrieves the value of a key value pair specified under a category in robot-configuration.yaml
     * @param category to retrieves value from
     * @param key for the value to be returned
     * @return the desired value
     */
    @Override
    public Object get(String category, String key) {
        ensureExists(category, key);
        return data.get(category).get(key);
    }

    /**
     * @param category the desired category
     * @return a map of all the key value pairs in the specified category
     */
    @Override
    public Map<String, Object> getCategory(String category) {
        ensureCategoryExists(category);
        return data.get(category);
    }

    /**
     * Retrieves an int from a key value pair specified under a category in robot-configuration.yaml
     * @param category to retrieves value from
     * @param key for the value to be returned
     * @return the desired value if it exists and is of type int
     */

    @Override
    public int getInt(String category, String key) {
        ensureExists(category, key);
        try {
            return (int) data.get(category).get(key);
        } catch (Exception ex) {
            if (data.get(category).get(key) == null) {
                throw new ConfigurationInvalidTypeException("int", key, "null");
            } else {
                throw new ConfigurationInvalidTypeException("int", key, data.get(category).get(key));
            }
        }
    }

    /**
     * Retrieves a double from a key value pair specified under a category in robot-configuration.yaml
     * @param category to retrieves value from
     * @param key for the value to be returned
     * @return the desired value if it exists and is of type double
     */

    @Override
    public double getDouble(String category, String key) {
        ensureExists(category, key);
        try {
            return (double) data.get(category).get(key);
        } catch (Exception ex) {
            if (data.get(category).get(key) == null) {
                throw new ConfigurationInvalidTypeException("double", key, "null");
            } else {
                throw new ConfigurationInvalidTypeException("double", key, data.get(category).get(key));
            }
        }
    }

    /**
     * Retrieves a boolean from a key value pair specified under a category in robot-configuration.yaml
     * @param category to retrieves value from
     * @param key for the value to be returned
     * @return the desired value if it exists and is of type boolean
     */

    @Override
    public boolean getBoolean(String category, String key) {
        ensureExists(category, key);
        try {
            return (boolean) data.get(category).get(key);
        } catch (Exception ex) {
            if (data.get(category).get(key) == null) {
                throw new ConfigurationInvalidTypeException("Boolean", key, "null");
            } else {
                throw new ConfigurationInvalidTypeException("Boolean", key, data.get(category).get(key));
            }
        }
    }

    /**
     * Retrieves a String from a key value pair specified under a category in robot-configuration.yaml
     * @param category to retrieves value from
     * @param key for the value to be returned
     * @return the desired value if it exists and is of type String
     */

    @Override
    public String getString(String category, String key) {
        ensureExists(category, key);
        try {
            return (String) data.get(category).get(key);
        } catch (Exception ex) {
            if (data.get(category).get(key) == null) {
                throw new ConfigurationInvalidTypeException("String", key, "null");
            } else {
                throw new ConfigurationInvalidTypeException("String", key, data.get(category).get(key));
            }
        }
    }

    /**
     * Retrieves a List from a key value pair specified under a category in robot-configuration.yaml
     * @param category to retrieves value from
     * @param key for the value to be returned
     * @return the desired value as a List if it exists
     */

    @Override
    public <T> List<T> getList(String category, String key) {
        ensureExists(category, key);
        try {
            return (List<T>) data.get(category).get(key);
        } catch (Exception ex) {
            throw new ConfigurationInvalidTypeException("int", key, data.get(key));
        }
    }

    /**
     * Retrieves a Map from a key value pair specified under a category in robot-configuration.yaml
     * @param category to retrieves value from
     * @param key for the value to be returned
     * @return the desired value as a Map if it exists
     */

    @Override
    public <K, V> Map<K, V> getMap(String category, String key) {
        ensureExists(category, key);
        try {
            return (Map<K, V>) data.get(category).get(key);
        } catch (Exception ex) {
            throw new ConfigurationInvalidTypeException("int", key, data.get(key));
        }
    }

    /**
     * Retrieves a Set from a key value pair specified under a category in robot-configuration.yaml
     * @param category to retrieves value from
     * @param key for the value to be returned
     * @return the desired value as a Set if it exists
     */

    @Override
    public <T> Set<T> getSet(String category, String key) {
        ensureExists(category, key);
        try {
            return new HashSet<T>((List<T>) data.get(category).get(key));
        } catch (ClassCastException ex) {
            throw new ConfigurationInvalidTypeException("set", key, data.get(category).get(key));
        }
    }


    /**
     * Retrieves an Enum from a key value pair specified under a category in robot-configuration.yaml
     * @param category to retrieves value from
     * @param key for the value to be returned
     * @return the desired value as an Enum if it exists
     */
    @Override
    public <T extends Enum<T>> T getEnum(String category, String key, Class<T> enumClass) {
        String value = getString(category, key).toUpperCase();

        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException ex) {
            throw new ConfigurationInvalidTypeException("enum", key, value);
        }
    }

    /**
     * @return the class loader
     */
    protected ClassLoader getClassLoader() {
        return YamlConfigParser.class.getClassLoader();
    }

    /**
     * Checks to see if a key exists in a category
     * Private method, to use this functionality from another class see .contains()
     * @param category the category to look in
     * @param key the key to check if it exists
     */
    private void ensureExists(String category, String key) {
        ensureCategoryExists(category);
        if (!data.get(category).containsKey(key)) {
            throw new ConfigurationException("***** No value found for key  '" + key + "' in category '" + category + "' *****");
        }
    }

    /**
     * Checks to see if a category exists
     * @param category the category check if it exists
     */
    private void ensureCategoryExists(String category) {
        if (!data.containsKey(category)) {
            throw new ConfigurationException("***** No category '" + category + "' found in SharedRobotConfiguration *****");
        }
    }

    /**
     * @return all data as a string
     */
    public String toString() {
        return data.toString();
    }

    /**
     * Public method to check if a key exists in a category
     * @param category the category to look in
     * @param key the key to check if it exists
     * @return true if it exists, false if it doesn't
     */
    public boolean contains(String category, String key) {
        return data.get(category).containsKey(key);
    }

    /**
     * Public method to checking if a category is empty
     * @param category the category to check
     * @return true if the category doesn't exist or if it contains no data
     */
    public boolean categoryIsEmpty(String category) {
        return !data.containsKey(category) || data.get(category) == null;
    }
}
