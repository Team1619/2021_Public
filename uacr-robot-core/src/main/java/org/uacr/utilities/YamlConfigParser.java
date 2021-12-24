package org.uacr.utilities;


import org.uacr.models.exceptions.ConfigurationException;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles reading in and distributing the information in the config files
 * Handles replacing values that are overridden in a variation
 */

public class YamlConfigParser {

    private static final Logger logger = LogManager.getLogger(YamlConfigParser.class);

    private final Yaml yaml;

    private Map<String, Map<String, Object>> data;
    private Map<String, String> nameTypes;
    private String robotVariation;

    public YamlConfigParser() {
        yaml = new Yaml();

        data = new HashMap<>();
        nameTypes = new HashMap<>();
        robotVariation = "none";
    }

    /**
     * Loads a ymal file and checks if there is a specified variation
     * @param path the location of the config file, all that usually needs to be specified is the name of the file
     */
    public void loadWithFolderName(String path) {
        YamlConfigParser parser = new YamlConfigParser();
        parser.load("general.yaml");
        Config config = parser.getConfig("robot");

        robotVariation = config.getString("robot_variation", "");

        load(path);
    }

    /**
     * Loads a ymal file into data, checks if there is a variation being used and values specified for that variation, removes the variations data once it has been used
     * @param path the location of the config file, all that usually needs to be specified is the name of the file
     */
    public void load(String path) {

        logger.trace("Loading config file '{}'", path);

        try {
            data = yaml.load(getClassLoader().getResourceAsStream(path));
        } catch (Throwable t) {
            logger.error(t.getMessage());
        }

        if (data == null) {
            data = new HashMap<>();
        }

        logger.trace("Loaded config file '{}'", path);

        nameTypes = new HashMap<>();

        for (Map.Entry<String, Map<String, Object>> entry : data.entrySet()) {
            if (entry.getValue() != null) {
                for (String name : entry.getValue().keySet()) {
                    if (!entry.getKey().equals("variations")) {
                        nameTypes.put(name, entry.getKey());
                    }
                }
            }
        }

        if (!robotVariation.equals("") && data.containsKey("variations")) {
            Map<String, Object> variation = (Map<String, Object>) data.get("variations").get(robotVariation);
            if (variation != null) {
                loadVariation(variation, new ArrayList<>());
            }
        }

        data.remove("variations");
    }

    /**
     * Finds the values listed in the variations and figures out the path to that value
     * Then calls the editMapValueWithStack method to replace the value for this key in data with the value from the variation
     * This is a recursive function
     * @param variation what variation to use
     * @param stack an ArrayList to hold the path to a particular value so it can be retraced to find the value to replace
     */
    private void loadVariation(Map<String, Object> variation, ArrayList<String> stack) {
        for (Map.Entry<String, Object> entry : variation.entrySet()) {
            if (entry.getValue() instanceof Map) {
                stack.add(entry.getKey());
                loadVariation((Map<String, Object>) entry.getValue(), stack);
                stack.remove(entry.getKey());
            } else {
                stack.add(entry.getKey());
                editMapValueWithStack(data, (ArrayList<String>) stack.clone(), entry.getValue());
                stack.remove(entry.getKey());
            }
        }
    }

    /**
     * Replaces a value in data with one specified in the variation
     * This is a recursive function
     * @param data data
     * @param stack the location of the value
     * @param value the value to change it to
     */
    private void editMapValueWithStack(Map data, ArrayList<String> stack, Object value) {
        if (stack.size() <= 1) {
            data.put(stack.get(0), value);
            return;
        }

        String key = stack.get(0);
        stack.remove(0);

        editMapValueWithStack((Map) data.get(key), stack, value);
    }

    /**
     * @param object the object that the config is wanted for
     * @return a new Config with the appropriate data from data for that object
     */
    public Config getConfig(Object object) {
        String name = object.toString().toLowerCase();

        logger.trace("Getting config with name '{}'", name);

        if (!(nameTypes.containsKey(name) && data.containsKey(nameTypes.get(name)))) {
            throw new ConfigurationException("***** No config exists for name '" + name + "' *****");
        }

        String type = nameTypes.get(name);
        try {
            return new Config(type, (Map) data.get(type).get(name));
        } catch (ClassCastException ex) {
            throw new ConfigurationException("***** Expected map but found " + data.getClass().getSimpleName() + "*****");
        }
    }

    /**
     * @return data
     */
    public Map getData() {
        return data;
    }

    /**
     * @return the ClassLoader for this file
     */
    protected ClassLoader getClassLoader() {
        return YamlConfigParser.class.getClassLoader();
    }
}
