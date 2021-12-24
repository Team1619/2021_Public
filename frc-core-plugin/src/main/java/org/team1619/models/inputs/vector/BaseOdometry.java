package org.team1619.models.inputs.vector;

import org.uacr.models.inputs.vector.InputVector;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.utilities.Config;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;
import org.uacr.utilities.purepursuit.Pose2d;
import org.uacr.utilities.purepursuit.Vector;

import java.util.Map;

public abstract class BaseOdometry extends InputVector {

    protected static final Logger LOGGER = LogManager.getLogger(BaseOdometry.class);

    protected final Config config;
    protected final InputValues sharedInputValues;
    protected final UpdateMode updateMode;
    private final String newUpdateKey;
    private final HeadingMode headingMode;

    private Pose2d rawPosition;
    private Pose2d positionUpdate;
    private Pose2d positionOffset;
    private Pose2d currentPosition;
    private Pose2d lastPosition;
    private Vector deltaPosition;

    public BaseOdometry(Object name, Config config, InputValues inputValues, UpdateMode updateMode, HeadingMode headingMode) {
        super(name, config);

        this.config = config;
        this.sharedInputValues = inputValues;
        this.updateMode = updateMode;
        newUpdateKey = name + "_new_position";
        this.headingMode = headingMode;

        rawPosition = new Pose2d();
        positionUpdate = new Pose2d();
        positionOffset = new Pose2d();
        currentPosition = new Pose2d();
        lastPosition = new Pose2d();
        deltaPosition = new Vector();
    }

    public BaseOdometry(Object name, Config config, InputValues inputValues, UpdateMode updateMode) {
        this(name, config, inputValues, updateMode, HeadingMode.ROBOT_CENTRIC);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void update() {
        Map<String, Double> newPositionData = sharedInputValues.getVector(newUpdateKey);
        if(!newPositionData.isEmpty()) {
            zero();

            positionOffset = new Pose2d(newPositionData.get("x"), newPositionData.get("y"), newPositionData.get("heading"));
            lastPosition = positionOffset;
            sharedInputValues.setVector(newUpdateKey, Map.of());
        }

        positionUpdate = getPositionUpdate();

        if(UpdateMode.DELTA_POSITION == updateMode) {
            rawPosition = new Pose2d(rawPosition.add(positionUpdate), positionUpdate.getHeading());
        } else {
            rawPosition = positionUpdate;
        }

        lastPosition = currentPosition;
        currentPosition = new Pose2d(new Vector(new Vector(rawPosition).rotate(HeadingMode.ROBOT_CENTRIC == headingMode ? positionOffset.getHeading() : 0).add(positionOffset)), rangeAngle(rawPosition.getHeading() + positionOffset.getHeading()));
        deltaPosition = new Vector(currentPosition.subtract(lastPosition));
    }

    @Override
    public Map<String, Double> get() {
        return Map.of("x", currentPosition.getX(), "y", currentPosition.getY(), "dx", deltaPosition.getX(), "dy", deltaPosition.getY(), "heading", currentPosition.getHeading());
    }

    @Override
    public void processFlag(String flag) {
        if("zero".equals(flag)) {
            zero();
            positionOffset = new Pose2d();
            rawPosition = new Pose2d();
            currentPosition = new Pose2d();
            lastPosition = new Pose2d();
        }
    }

    protected Pose2d getRawPosition() {
        return rawPosition;
    }

    protected Pose2d getPositionOffset() {
        return positionOffset;
    }

    protected double rangeAngle(double position) {
        return (((position % 360) + 540) % 360) - 180;
    }

    protected abstract Pose2d getPositionUpdate();

    protected abstract void zero();

    protected enum UpdateMode {
        ABSOLUTE_POSITION,
        DELTA_POSITION
    }

    protected enum HeadingMode {
        FIELD_CENTRIC,
        ROBOT_CENTRIC
    }
}
