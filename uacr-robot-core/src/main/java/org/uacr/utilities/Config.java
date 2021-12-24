package org.uacr.utilities;

import org.uacr.models.exceptions.ConfigurationException;
import org.uacr.models.exceptions.ConfigurationInvalidTypeException;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds the configuration data for a specific object
 * Allows for easy access to that data
 * Created for each object by the YmalConfigParser
 */

public class Config {

    private final Map<String, Object> data;
    private final String type;

    /**
     * @param type the type of object (the category it is listed under in the config file) such as "controller_button" or "single_state"
     * @param data the configuration data for the object
     */
    public Config(String type, @Nullable Map data) {
        this.type = type;

        if (data == null) {
            data = new HashMap<>();
        }
        this.data = data;
    }

    /**
     * @return the type of object (the category it is listed under in the config file) such as "controller_button" or "single_state"
     */
    public String getType() {
        return type;
    }

    /**
     * @return the data for the object
     */
    public Map<String, Object> getData() {
        return data;
    }

    /**
     * Retrieves a value from the data of any type
     * @param key the key for the desired value
     * @return the value (of any type)
     */
    public Object get(String key) {
        ensureExists(key);
        return data.get(key);
    }

    /**
     * Retrieves a value from the data of any type if the key exists, otherwise returns the specified default value
     * @param key the key for the desired value
     * @param defaultValue the value to return in the key doesn't exist
     * @return the value (of any type)
     */
    public Object get(String key, Object defaultValue) {
        try {
            return data.getOrDefault(key, defaultValue);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    /**
     * Retrieves a value from the data as an int
     * @throws ConfigurationInvalidTypeException If the value is not castable as type int or is null
     * @param key the key for the desired value
     * @return the value as an int
     */
    public int getInt(String key) {
        ensureExists(key);
        try {
            return (int) data.get(key);
        } catch (Exception ex) {
            if (data.get(key) == null) {
                throw new ConfigurationInvalidTypeException("int", key, "null");
            } else {
                throw new ConfigurationInvalidTypeException("int", key, data.get(key));
            }
        }
    }

    /**
     * Retrieves a value from the data as an int or returns the specified default value if the value is null
     * @throws ConfigurationInvalidTypeException If the value is not castable as type int or is null
     * @param key the key for the desired value
     * @param defaultValue the value to be returned if the desired value is null
     * @return the value as a int
     */
    public int getInt(String key, int defaultValue) {
        try {
            if (data.get(key) == null) {
                return defaultValue;
            }
            return (int) data.getOrDefault(key, defaultValue);
        } catch (Exception ex) {
            try {
                throw new ConfigurationInvalidTypeException("int", key, data.get(key));
            } catch (NullPointerException e) {
                return defaultValue;
            }
        }
    }

    /**
     * Retrieves a value from the data as a double
     * @throws ConfigurationInvalidTypeException If the value is not castable as type double or is null
     * @param key the key for the desired value
     * @return the value as a double
     */
    public double getDouble(String key) {
        ensureExists(key);
        try {
            return (double) data.get(key);
        } catch (Exception ex) {
            if (data.get(key) == null) {
                throw new ConfigurationInvalidTypeException("double", key, "null");
            } else {
                throw new ConfigurationInvalidTypeException("double", key, data.get(key));
            }
        }
    }

    /**
     * Retrieves a value from the data as a double or returns the specified default value if the value is null
     * @throws ConfigurationInvalidTypeException If the value is not castable as type double or is null
     * @param key the key for the desired value
     * @param defaultValue the value to be returned if the desired value is null
     * @return the value as a double
     */
    public double getDouble(String key, double defaultValue) {
        try {
            if (data.get(key) == null) {
                return defaultValue;
            }
            return (double) data.getOrDefault(key, defaultValue);
        } catch (Exception ex) {
            try {
                throw new ConfigurationInvalidTypeException("double", key, data.get(key));
            } catch (NullPointerException e) {
                return defaultValue;
            }
        }
    }

    /**
     * Retrieves a value from the data as a boolean
     * @throws ConfigurationInvalidTypeException If the value is not castable as type boolean or is null
     * @param key the key for the desired value
     * @return the value as a boolean
     */
    public boolean getBoolean(String key) {
        ensureExists(key);
        try {
            return (boolean) data.get(key);
        } catch (Exception ex) {
            if (data.get(key) == null) {
                throw new ConfigurationInvalidTypeException("boolean", key, "null");
            } else {
                throw new ConfigurationInvalidTypeException("boolean", key, data.get(key));
            }
        }
    }

    /**
     * Retrieves a value from the data as a boolean or returns the specified default value if the value is null
     * @throws ConfigurationInvalidTypeException If the value is not castable as type boolean or is null
     * @param key the key for the desired value
     * @param defaultValue the value to be returned if the desired value is null
     * @return the value as a boolean
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        try {
            if (data.get(key) == null) {
                return defaultValue;
            }
            return (boolean) data.getOrDefault(key, defaultValue);
        } catch (Exception ex) {
            try {
                throw new ConfigurationInvalidTypeException("boolean", key, data.get(key));
            } catch (NullPointerException e) {
                return defaultValue;
            }
        }
    }

    /**
     * Retrieves a value from the data as a String
     * @throws ConfigurationInvalidTypeException If the value is not castable as type String or is null
     * @param key the key for the desired value
     * @return the value as a String
     */
    public String getString(String key) {
        ensureExists(key);
        try {
            return (String) data.get(key);
        } catch (Exception ex) {
            if (data.get(key) == null) {
                throw new ConfigurationInvalidTypeException("string", key, "null");
            } else {
                throw new ConfigurationInvalidTypeException("string", key, data.get(key));
            }
        }
    }

    /**
     * Retrieves a value from the data as a String or returns the specified default value if the value is null
     * @throws ConfigurationInvalidTypeException If the value is not castable as type String or is null
     * @param key the key for the desired value
     * @param defaultValue the value to be returned if the desired value is null
     * @return the value as a String
     */
    public String getString(String key, String defaultValue) {
        try {
            if (data.get(key) == null) {
                return defaultValue;
            }
            return (String) data.getOrDefault(key, defaultValue);
        } catch (Exception ex) {
            try {
                throw new ConfigurationInvalidTypeException("string", key, data.get(key));
            } catch (NullPointerException e) {
                return defaultValue;
            }
        }
    }

    /**
     * Retrieves a value from the data as a List
     * @throws ConfigurationInvalidTypeException If the value is not castable as type List or is null
     * @param key the key for the desired value
     * @return the value as a List
     */
    public List getList(String key) {
        ensureExists(key);
        try {
            return (List) data.get(key);
        } catch (Exception ex) {
            try {
                throw new ConfigurationInvalidTypeException("list", key, data.get(key));
            } catch (Exception e) {
                throw new ConfigurationInvalidTypeException("list", key, "null");
            }
        }
    }

    /**
     * Retrieves a value from the data as a List or returns the specified default value if the value is null
     * @throws ConfigurationInvalidTypeException If the value is not castable as type List or is null
     * @param key the key for the desired value
     * @param defaultValue the value to be returned if the desired value is null
     * @return the value as a List
     */
    public List getList(String key, List defaultValue) {
        try {
            if (data.get(key) == null) {
                return defaultValue;
            }
            return (List) data.getOrDefault(key, defaultValue);
        } catch (Exception ex) {
            try {
                throw new ConfigurationInvalidTypeException("list", key, data.get(key));
            } catch (NullPointerException e) {
                return defaultValue;
            }
        }
    }

    /**
     * Retrieves a value from the data as an Enum
     * @throws ConfigurationInvalidTypeException If the value is not castable as type Enum or is null
     * @param key the key for the desired value
     * @return the value as an Enum
     */
    public <T extends Enum<T>> T getEnum(String key, Class<T> enumClass) {
        String value = getString(key).toUpperCase();

        try {
            return Enum.valueOf(enumClass, value);
        } catch (Exception ex) {
            try {
                throw new ConfigurationInvalidTypeException("enum", key, data.get(key));
            } catch (Exception e) {
                throw new ConfigurationInvalidTypeException("enum", key, "null");
            }
        }
    }

    /**
     * Retrieves a value from the data as an Enum or returns the specified default value if the value is null
     * @throws ConfigurationInvalidTypeException If the value is not castable as type Enum or is null
     * @param key the key for the desired value
     * @param defaultValue the value to be returned if the desired value is null
     * @return the value as an Enum
     */
    public <T extends Enum<T>> T getEnum(String key, Class<T> enumClass, T defaultValue) {
        String value = getString(key, defaultValue.toString()).toUpperCase();

        try {
            return Enum.valueOf(enumClass, value);
        } catch (Exception ex) {
            try {
                throw new ConfigurationInvalidTypeException("enum", key, data.get(key));
            } catch (NullPointerException e) {
                return defaultValue;
            }
        }
    }

    /**
     * Creates a config with the configuration information contained in a deeper level of the current config
     * @param key the key to identify the subconfig to be created
     * @param type the type of data
     * @return a new instance of Config with the info in this subcategory of the config
     */
    public Config getSubConfig(String key, String type) {
        ensureExists(key);
        try {
            return new Config(type, (Map) data.get(key));
        } catch (ClassCastException ex) {
            throw new ConfigurationException("***** Value for " + key + " was not a config *****");
        }
    }

    /**
     * The method used by other classes to check if a key exists
     * @param key the key to check
     * @return whether or not that key is in the data map for this config
     */
    public boolean contains(String key) {
        return data.containsKey(key);
    }

    /**
     * The internal method for checking if a key exists
     * @param key the key to check
     * @throws ConfigurationException if the key is not in the data map
     */
    private void ensureExists(String key) {
        if (!data.containsKey(key)) {
            throw new ConfigurationException("***** Must provide a value for " + key + " for configuring " + type + " ***** ");
        }
    }

    /**
     * @return all the data in the map as a string
     */
    public String toString() {
        return data.toString();
    }
}