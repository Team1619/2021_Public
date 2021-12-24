package org.team1619.models.inputs.vector;

import org.uacr.models.inputs.vector.InputVector;
import org.uacr.utilities.Config;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

import java.util.HashMap;
import java.util.Map;

public abstract class Encoder extends InputVector {

    private static final Logger LOGGER = LogManager.getLogger(Encoder.class);

    protected final Config config;
    protected final int deviceNumber;
    protected final boolean readPosition;
    protected final boolean readAbsolutePosition;
    protected final boolean readVelocity;
    protected final boolean bootToAbsolutePosition;
    protected final boolean sensorRange360;
    protected final boolean inverted;
    protected final double positionScalar;
    protected final double velocityScalar;
    protected final double magnetOffset;

    protected  Map<String, Double> encoderValues;

    public Encoder(Object name, Config config){
        super (name, config);
        this.config = config;
        deviceNumber = config.getInt("device_number");
        magnetOffset = config.getDouble("magnet_offset", 0.0);
        readPosition = config.getBoolean("read_position", false);
        readAbsolutePosition = config.getBoolean("read_absolute_position", false);
        readVelocity = config.getBoolean("read_velocity", false);
        sensorRange360 = config.getBoolean("sensor_range_360", false);
        inverted = config.getBoolean("inverted", false);
        positionScalar = config.getDouble("position_scalar", 1.0);
        velocityScalar = config.getDouble("velocity_scalar", 1.0);
        bootToAbsolutePosition = config.getBoolean("boot_to_absolute_position", true);
        encoderValues = new HashMap<>();
    }

    @Override
    public void initialize() {
    }

    @Override
    public void update() {
        encoderValues = readHardware();
    }

    @Override
    public Map<String, Double> get() {
        return encoderValues;
    }

    @Override
    public void processFlag(String flag) {
        if (flag.equals("zero")) {
            zeroEncoder();
        }
    }

    protected abstract void zeroEncoder();

    protected abstract Map<String,Double> readHardware();


}
