package org.uacr.models.exceptions;

/**
 * Exception thrown when a type in yaml doesn't match the value requested in java
 */

public class ConfigurationInvalidTypeException extends ConfigurationException {

    public ConfigurationInvalidTypeException(String type, String key, Object data) {
        super("******** Expected " + type + " for " + key + " found " + data.getClass().getSimpleName() + " ********");
    }

    public ConfigurationInvalidTypeException(String type, String key, String found) {
        super("******** Expected " + type + " for " + key + " found " + found + " ********");
    }
}
