package org.uacr.utilities;

/**
 * Tracks the amount of elapsed time in milliseconds after timer has been started
 * Can be reset and used again
 */
public class Timer {

    private long startTime;
    private long time;

    public Timer() {
        startTime = -1;
        time = 0;
    }

    /**
     * Stores the system time when the timer is started
     * @param timeMs the duration of time to elapse before the timer is complete in milliseconds
     */
    public void start(long timeMs) {
        time = timeMs;
        startTime = RobotSystem.currentTimeMillis();
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
        return startTime != -1;
    }

    /**
     * @return true if the timer has been started and the current system time minus the system time when the timer was started is greater than or equal to the length of time
     * the timer was set for
     */

    public boolean isDone() {
        return isStarted() && RobotSystem.currentTimeMillis() - startTime >= time;
    }
}
