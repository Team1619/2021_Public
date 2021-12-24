package org.uacr.utilities.services;

/**
 * Keeps track of the timing for services
 */


public class Scheduler {

    private final TimeUnit timeUnit;
    private final double initialDelayNanos;
    private final double standardDelayNanos;

    private long startTime = 0;
    private long lastTime = 0;

    /**
     * @param standardDelay: min time each frame can run
     */
    public Scheduler(double standardDelay) {
        this(0, standardDelay);
    }

    /**
     * @param standardDelay min time each frame can run
     * @param initialDelay delay before first frame
     */
    public Scheduler(double initialDelay, double standardDelay) {
        this(initialDelay, standardDelay, TimeUnit.MILLISECOND);
    }
    /**
     * @param standardDelay min time each frame can run
     * @param timeUnit milliseconds, seconds, minutes
     */
    public Scheduler(double standardDelay, TimeUnit timeUnit) {
        this(0, standardDelay, timeUnit);
    }

    public Scheduler(double initialDelay, double standardDelay, TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        initialDelayNanos = timeUnit.toNanoseconds(initialDelay);
        standardDelayNanos = timeUnit.toNanoseconds(standardDelay);

        startTime = -1;
        lastTime = -1;
    }

    /**
     * Called on start-up
     */
    public synchronized void start() {
        startTime = System.nanoTime();
    }

    /**
     * Called every frame
     */
    public synchronized void run() {
        startTime = 0;
        lastTime = System.nanoTime();
    }

    /**
     * @return true when the service has reached it's min time
     */
    public synchronized boolean shouldRun() {
        long currentTime = System.nanoTime();

        long nanosSinceLastRun = currentTime - startTime;

        if (startTime != 0 && nanosSinceLastRun < initialDelayNanos) {
            return false;
        }

        nanosSinceLastRun = currentTime - lastTime;

        return lastTime == 0 || nanosSinceLastRun >= standardDelayNanos;
    }

    /**
     * @return the amount of time until the next time the service should run
      */
    public synchronized long nanosUntilNextRun() {
        long currentTime = System.nanoTime();

        long time = nextRunTimeNanos() - currentTime;

        if (time < 0) {
            return 0;
        }

        return time;
    }

    /**
     * @return when to start the next frame
     */
    public synchronized long nextRunTimeNanos() {
        if (startTime != 0) {
            long time = (int) (lastTime + initialDelayNanos);
            if (time < 0) {
                return 0;
            }
            return time;
        }

        long time = (long) (lastTime + standardDelayNanos);
        if (time < 0) {
            return 0;
        }
        return time;
    }

    /**
     * Stores the conversions to nanoseconds
     * Converts to nanoseconds
     */
    public enum TimeUnit {
        MINUTE(60000000000L),
        SECOND(1000000000),
        MILLISECOND(1000000);

        private final long toNanoseconds;

        TimeUnit(long toNanoseconds) {
            this.toNanoseconds = toNanoseconds;
        }

        /**
         * Converts time to nanoseconds
         * @param time the time to convert
         * @return the specified time in nanoseconds
         */
        public synchronized double toNanoseconds(double time) {
            return time * toNanoseconds;
        }
    }
}
