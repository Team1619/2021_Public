package org.uacr.utilities.services;

/**
 * Defines the different states a service can be in
 */

public enum ServiceState {
    AWAITING_START,
    STARTING,
    RUNNING,
    STOPPING,
    STOPPED
}
