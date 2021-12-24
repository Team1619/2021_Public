package org.team1619.models.inputs.vector;

import org.uacr.shared.abstractions.InputValues;
import org.uacr.utilities.Config;
import org.uacr.utilities.purepursuit.Point;
import org.uacr.utilities.purepursuit.Pose2d;
import org.uacr.utilities.purepursuit.Vector;

import java.util.HashMap;
import java.util.Map;

public class NetworkTableOdometry extends BaseOdometry {

    private final String networkTablesInput;

    private double valid;
    private Vector visionPosition;
    private Vector offset;

    public NetworkTableOdometry(Object name, Config config, InputValues inputValues) {
        super(name, config, inputValues, UpdateMode.ABSOLUTE_POSITION);

        networkTablesInput = config.getString("network_table_input");

        initialize();

        visionPosition = new Vector();
        offset = new Vector();
    }

    @Override
    public void initialize() {
        valid = 0.0;
    }

    @Override
    public Pose2d getPositionUpdate() {
        Map<String, Double> networkTablesValues = sharedInputValues.getVector(networkTablesInput);

        valid = networkTablesValues.get("valid");

        visionPosition = new Vector(new Point(networkTablesValues.get("x"), networkTablesValues.get("y")));
        return new Pose2d(visionPosition.add(offset), 0.0);
    }

    @Override
    protected void zero() {
        offset = new Vector(new Vector().subtract(visionPosition));
    }

    @Override
    public Map<String, Double> get() {
        Map<String, Double> data = new HashMap<>(super.get());

        data.put("valid", valid);

        return data;
    }
}
