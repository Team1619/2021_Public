package org.team1619.models.inputs.vector.sim;

import org.team1619.models.inputs.vector.NetworkTableReader;
import org.uacr.utilities.Config;

import java.util.HashMap;
import java.util.Map;

public class SimNetworkTableReader extends NetworkTableReader {

    /**
     * @param name   the name of the InputVector.
     * @param config the configuration for the InputVector.
     */
    public SimNetworkTableReader(Object name, Config config) {
        super(name, config);
    }

    @Override
    public Map<String, Double> getData() {
        Map<String, Double> data = new HashMap<>();

        values.entrySet().stream().forEach(value -> data.put(value.getValue(), 0.0));

        return data;
    }
}
