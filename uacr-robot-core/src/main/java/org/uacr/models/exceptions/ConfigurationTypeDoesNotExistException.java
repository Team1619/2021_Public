package org.uacr.models.exceptions;

/**
 * Exception thrown when java tries to read a yaml value that isn't specified
 */

public class ConfigurationTypeDoesNotExistException extends ConfigurationException {

    public ConfigurationTypeDoesNotExistException(String type) {
        super("Type " + type + " does not exsist");
    }
}
