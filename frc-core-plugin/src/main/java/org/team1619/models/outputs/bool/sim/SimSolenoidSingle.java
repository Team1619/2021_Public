package org.team1619.models.outputs.bool.sim;

import org.team1619.models.outputs.bool.SolenoidSingle;
import org.uacr.utilities.Config;

public class SimSolenoidSingle extends SolenoidSingle {

    public SimSolenoidSingle(Object name, Config config) {
        super(name, config);
    }

    @Override
    public void processFlag(String flag) {

    }

    @Override
    public void setHardware(boolean output) {

    }
}
