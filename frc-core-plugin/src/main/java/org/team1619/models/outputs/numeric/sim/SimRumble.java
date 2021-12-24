package org.team1619.models.outputs.numeric.sim;

import org.team1619.models.outputs.numeric.Rumble;
import org.uacr.utilities.Config;

/**
 * SimRumble extends Rumble, and acts like xbox controller rumble motors in sim mode
 */

public class SimRumble extends Rumble {

    private double output;
    private double adjustedOutput;
    private String rumbleSide;

    public SimRumble(Object name, Config config) {
        super(name, config);

        output = 0.0;
        adjustedOutput = 0.0;
        rumbleSide = "none";
    }

    @Override
    public void processFlag(String flag) {

    }

    @Override
    public void setHardware(String outputType, double outputValue, String profile) {
        adjustedOutput = outputValue;
        if (rumbleSide.equals("right")) {
            output = adjustedOutput;
            rumbleSide = "right";
        } else if (rumbleSide.equals("left")) {
            output = adjustedOutput;
            rumbleSide = "left";
        }
    }
}