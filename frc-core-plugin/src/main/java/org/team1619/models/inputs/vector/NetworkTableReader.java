package org.team1619.models.inputs.vector;

import org.uacr.models.inputs.vector.InputVector;
import org.uacr.utilities.Config;

import java.util.HashMap;
import java.util.Map;

public abstract class NetworkTableReader extends InputVector {

    protected final Map<String, String> values;

    private Map<String, Double> data;

    /**
     * @param name   the name of the InputVector.
     * @param config the configuration for the InputVector.
     */
    public NetworkTableReader(Object name, Config config) {
        super(name, config);

        values = new HashMap<>();

        for(Map.Entry<String, Object> value : config.getSubConfig("values", "values_config").getData().entrySet()) {
            values.put(value.getKey(), value.getValue().toString());
        }
    }

    @Override
    public void initialize() {

    }

    @Override
    public void update() {
        data = getData();
    }

    @Override
    public Map<String, Double> get() {
        return data;
    }

    @Override
    public void processFlag(String flag) {
        if("zero".equals(flag)) {

        }
    }

    public abstract Map<String, Double> getData();
}
