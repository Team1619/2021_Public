package org.uacr.utilities.logging;

/**
 * Handles the log messages of a single class
 */

public class Logger {

    private final LogManager logManager;
    private final String prefix;

    protected Logger(LogManager logManager, String prefix) {
        this.logManager = logManager;
        this.prefix = prefix;
    }

    public void trace(String message, Object... args) {
        logManager.log(LogManager.Level.TRACE, prefix, message, args);
    }

    public void debug(String message, Object... args) {
        logManager.log(LogManager.Level.DEBUG, prefix, message, args);
    }

    public void info(String message, Object... args) {
        logManager.log(LogManager.Level.INFO, prefix, message, args);
    }

    public void error(String message, Object... args) {
        logManager.log(LogManager.Level.ERROR, prefix, message, args);
    }

    public void error(Exception message, Object... args) {
        error(message.toString(), args);
    }

    public void log(LogManager.Level level, String message, Object... args) {
        logManager.log(level, prefix, message, args);
    }
}