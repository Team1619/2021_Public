package org.uacr.utilities.services;

/**
 * Configures one service to run on a scheduler
 */

public interface ScheduledService extends Service {

    Scheduler scheduler();
}
