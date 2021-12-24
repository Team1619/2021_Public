package org.uacr.utilities;

import static org.uacr.utilities.RobotSystem.RuntimeMode.*;

public class RobotSystem {

    public enum RuntimeMode {SIM_MODE, HARDWARE_MODE}
    private static RuntimeMode runtimeMode = HARDWARE_MODE;
    private static final long MILLIS_PER_FRAME = 15;
    private static long currentTime = 0;

    public static long currentTimeMillis() {
        if (runtimeMode == SIM_MODE) {
            return currentTime;
        } else {
            return System.currentTimeMillis();
        }
    }

    public static void update(){
        if (runtimeMode == SIM_MODE) {
            currentTime += MILLIS_PER_FRAME;
        } else {
            currentTime = System.currentTimeMillis();
        }
    }

    public static void setRuntimeMode(RuntimeMode mode) {
        runtimeMode = mode;
    }

    public static RuntimeMode getRuntimeMode() {
        return runtimeMode;
    }

}
