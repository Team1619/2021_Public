package org.team1619.models.inputs.vector.sim;

import org.team1619.models.inputs.vector.Encoder;
import org.uacr.utilities.Config;

import java.util.HashMap;
import java.util.Map;

public class SimCanCoder extends Encoder {

    private final Map<String, Double> canCoderValues = new HashMap<>();

    public SimCanCoder(Object name, Config config) {
        super(name, config);

    }

    @Override
    public void initialize() {
        
    }

    @Override
    protected Map<String, Double> readHardware() {
        if(readPosition) {
            canCoderValues.put("position", 0.0);
        }
        if (readAbsolutePosition) {
            canCoderValues.put("absolute_position", 0.0);
        }
        if (readVelocity){
            canCoderValues.put("velocity", 0.0);
        }
        return canCoderValues;
    }

    @Override
    protected void zeroEncoder() {
    }
}
