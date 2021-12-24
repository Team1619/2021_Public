package org.team1619.models.inputs.vector.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.team1619.models.inputs.vector.NetworkTableReader;
import org.uacr.utilities.Config;

import java.util.HashMap;
import java.util.Map;

public class RobotNetworkTableReader extends NetworkTableReader {

    private final NetworkTable networkTable;

    /**
     * @param name   the name of the InputVector.
     * @param config the configuration for the InputVector.
     */
    public RobotNetworkTableReader(Object name, Config config) {
        super(name, config);

        networkTable = NetworkTableInstance.getDefault().getTable(config.getString("host"));
    }

    @Override
    public Map<String, Double> getData() {
        Map<String, Double> data = new HashMap<>();

        values.entrySet().forEach(value -> data.put(value.getValue(), networkTable.getEntry(value.getKey()).getDouble(0.0)));

        return data;
    }
}
