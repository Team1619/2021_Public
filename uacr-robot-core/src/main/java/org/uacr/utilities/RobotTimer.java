package org.uacr.utilities;

import org.uacr.shared.abstractions.InputValues;

/**
 * Tracks the amount of elapsed time in milliseconds after timer has been started
 * Can be reset and used again
 */

public class RobotTimer {

    private double startTime;
    private double duration;

    private final InputValues sharedInputValues;

    public RobotTimer(InputValues inputValues) {
        startTime = -1;
        duration = 0;

        sharedInputValues = inputValues;
    }

    /**
     * Stores the system time when the timer is started
     * @param durationMs the duration of time to elapse before the timer is complete in milliseconds
     */
    public void start(long durationMs) {
        duration = durationMs;
        startTime = sharedInputValues.getNumeric("ipn_frame_start_time");
    }

    /**
     * Clears the timer so it can be used again
     */

    public void reset() {
        startTime = -1;
    }

    /**
     * @return true if the timer has been started
     */
    public boolean isStarted() {
        return startTime > -1;
    }

    /**
     * @return true if the timer has been started and the current system time minus the system time when the timer was started is greater than or equal to the length of time
     * the timer was set for
     */

    public boolean isDone() {
        return isStarted() && sharedInputValues.getNumeric("ipn_frame_start_time") - startTime >= duration;
    }
}
