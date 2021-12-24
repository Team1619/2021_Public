package org.uacr.models.exceptions;

/**
 * Exception thrown when the robot is not properly configured in yaml
 */

public class ConfigurationException extends RuntimeException {

    /**
     * @param message the exception text
     */
    public ConfigurationException(String message) {
        super(message);
    }
}
