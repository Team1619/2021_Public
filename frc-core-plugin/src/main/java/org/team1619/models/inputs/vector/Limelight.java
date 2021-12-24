package org.team1619.models.inputs.vector;

import org.uacr.models.inputs.vector.InputVector;
import org.uacr.utilities.Config;

import java.util.HashMap;
import java.util.Map;

public abstract class Limelight extends InputVector {

    protected final Map<String, Integer> pipelines;
    private Map<String, Double> values;

    public Limelight(Object name, Config config) {
        super(name, config);

        pipelines = new HashMap<>();
        if (config.contains("pipelines")) {
            for (Map.Entry<String, Object> pipeline : config.getSubConfig("pipelines", "pipeline_config").getData().entrySet()) {
                pipelines.put(pipeline.getKey(), Integer.valueOf(pipeline.getValue().toString()));
            }
        }
        values = new HashMap<>();
    }

    @Override
    public void update() {
        values = getData();
    }

    @Override
    public Map<String, Double> get() {
        return values;
    }

    @Override
    public void processFlag(String flag) {

    }

    public abstract Map<String, Double> getData();
}
