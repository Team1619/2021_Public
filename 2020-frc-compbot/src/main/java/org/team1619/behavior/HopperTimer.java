package org.team1619.behavior;

import org.uacr.shared.abstractions.InputValues;
/**
 * Tracks the amount of elapsed time in milliseconds after timer has been started
 * Can be reset and used again
 */

public class HopperTimer {

    private double startTime;
    private double duration;

    private final InputValues sharedInputValues;

    public HopperTimer(InputValues inputValues) {
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
     * @return Milliseconds elapsed since timer started; 0 if timer has not started
     */
    public long getElapsed(){
        if(!isStarted()){
            return 0;
        }
        return (long)(sharedInputValues.getNumeric("ipn_frame_start_time") - startTime);
    }

    /**
     * @return Elapsed time as a percentage of total duration.
     */
    public double getPercentDone(){
        // When elapsed==0 and duration==0.0d, then dividing results in NaN, and min(NaN, 1.0) == Nan. Don't do this.
        // When elapsed > 0 and duration==0.0d, then dividing results in Infinity, and min(Infinity, 1.0) == 1.0. This is ok.
        if(!isStarted()){
            return 0.0;
        } else if (duration == 0.0) {
            return 1.0;
        } else {
            return Math.min((getElapsed() / duration), 1.0);
        }
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
        return isStarted() && getElapsed() >= duration;
    }
}
