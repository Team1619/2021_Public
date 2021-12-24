package org.team1619.models.outputs.bool.sim;

import org.team1619.models.outputs.bool.SolenoidDouble;
import org.uacr.utilities.Config;

public class SimSolenoidDouble extends SolenoidDouble {

    public SimSolenoidDouble(Object name, Config config) {
        super(name, config);
    }

    @Override
    public void processFlag(String flag) {

    }

    @Override
    public void setHardware(boolean output) {

    }
}
