package org.team1619.models.inputs.vector;

import org.uacr.shared.abstractions.InputValues;
import org.uacr.utilities.Config;
import org.uacr.utilities.purepursuit.Point;
import org.uacr.utilities.purepursuit.Pose2d;
import org.uacr.utilities.purepursuit.Vector;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class OdometryFuser extends BaseOdometry {

    private final InputValues inputValues;

    private final String relativeOdometryInput;
    private final String absoluteOdometryInput;
    private final int absoluteOdometryDelay;

    private final Map<Long, Vector> movementBuffer;

    private Vector lastAbsoluteOdometryPosition;
    private Vector relativeOdometryOffset;

    public OdometryFuser(Object name, Config config, InputValues inputValues) {
        super(name, config, inputValues, UpdateMode.ABSOLUTE_POSITION, HeadingMode.FIELD_CENTRIC);

        this.inputValues = inputValues;

        relativeOdometryInput = config.getString("relative_odometry_input");
        absoluteOdometryInput = config.getString("absolute_odometry_input");
        absoluteOdometryDelay = config.getInt("absolute_odometry_delay");

        movementBuffer = new HashMap<>();

        lastAbsoluteOdometryPosition = new Vector();
        relativeOdometryOffset = new Vector();
    }

    @Override
    public void initialize() {
        movementBuffer.clear();
    }

    @Override
    protected Pose2d getPositionUpdate() {
        long currentTime = System.currentTimeMillis();

        Map<String, Double> relativeOdometryValues = inputValues.getVector(relativeOdometryInput);
        Map<String, Double> absoluteOdometryValues = inputValues.getVector(absoluteOdometryInput);

        Vector relativeOdometryPosition = new Vector(new Point(relativeOdometryValues.get("x"), relativeOdometryValues.get("y"))).rotate(getPositionOffset().getHeading());
        Vector relativeOdometryDelta = new Vector(new Point(relativeOdometryValues.get("dx"), relativeOdometryValues.get("dy"))).rotate(getPositionOffset().getHeading());

        movementBuffer.put(currentTime, relativeOdometryDelta);

        movementBuffer.keySet().removeIf(vectorTime -> vectorTime < currentTime - absoluteOdometryDelay);

        if(absoluteOdometryValues.getOrDefault("valid", 0.0) == 1.0) {

            Vector absoluteOdometryPosition = new Vector(new Point(absoluteOdometryValues.get("x"), absoluteOdometryValues.get("y")));

            if(!absoluteOdometryPosition.equals(lastAbsoluteOdometryPosition)) {
                movementBuffer.values().stream().reduce((total, delta) -> new Vector(total.add(delta))).ifPresent(total -> {
                    relativeOdometryOffset = new Vector(absoluteOdometryPosition.add(total).subtract(relativeOdometryPosition));
                });
                lastAbsoluteOdometryPosition = absoluteOdometryPosition;
            }
        }

        return new Pose2d(relativeOdometryPosition.add(relativeOdometryOffset), relativeOdometryValues.get("heading"));
    }

    @Override
    protected void zero() {
        movementBuffer.clear();
        lastAbsoluteOdometryPosition = new Vector(Integer.MAX_VALUE, 0);
    }
}
