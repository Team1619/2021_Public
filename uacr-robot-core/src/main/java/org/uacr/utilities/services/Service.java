package org.uacr.utilities.services;

/**
 * Configures one service
 */

public interface Service {

    void startUp() throws Exception;

    void runOneIteration() throws Exception;

    void shutDown() throws Exception;
}
