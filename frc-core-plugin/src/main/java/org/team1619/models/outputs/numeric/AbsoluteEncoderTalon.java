package org.team1619.models.outputs.numeric;

import org.uacr.models.exceptions.ConfigurationInvalidTypeException;
import org.uacr.models.outputs.numeric.OutputNumeric;
import org.uacr.robot.AbstractModelFactory;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.utilities.Config;
import org.uacr.utilities.YamlConfigParser;

public class AbsoluteEncoderTalon extends OutputNumeric {

    private final InputValues inputValues;
    private final Talon talon;
    private final String absolutePositionInput;
    private final double minAbsolutePosition;
    private final double maxAbsolutePosition;
    private final double countsPerRev;
    private final double maxRotationDistance;

    private boolean isZeroing;
    private double positionOffset;

    public AbsoluteEncoderTalon(Object name, Config config, YamlConfigParser parser, AbstractModelFactory modelFactory, InputValues inputValues) {
        super(name, config);

        this.inputValues = inputValues;

        String talon = config.getString("talon");
        OutputNumeric motor = modelFactory.createOutputNumeric(talon, parser.getConfig(talon), parser);
        if (!(motor instanceof Talon)) {
            throw new ConfigurationInvalidTypeException("Talon", "master", motor);
        }
        this.talon = (Talon) motor;

        absolutePositionInput = config.getString("absolute_position_input");

        minAbsolutePosition = config.getDouble("min_absolute_position", -180);
        maxAbsolutePosition = config.getDouble("max_absolute_position", 180);

        if (minAbsolutePosition >= maxAbsolutePosition) {
            throw new RuntimeException("min_absolute_position must be less than max_absolute_position");
        }

        countsPerRev = maxAbsolutePosition - minAbsolutePosition;
        maxRotationDistance = countsPerRev / 2;

        isZeroing = false;
        positionOffset = 0.0;
    }

    @Override
    public void setHardware(String outputType, double outputValue, String profile) {
        if(isZeroing) {
            double talonPosition = talon.getSensorPosition();
            if (Math.abs(talonPosition) > 0.1) {
                talon.setHardware("percent", 0.0, "none");
                talon.processFlag("zero");
            } else {
                positionOffset = talonPosition - inputValues.getVector(absolutePositionInput).getOrDefault("absolute_position", 0.0);
                isZeroing = false;
            }
        } else {
            if ("absolute_position".equals(outputType)) {
                double requestedPosition = rangeEncoderPosition(outputValue);

                double relativePosition = talon.getSensorPosition();
                double rangedRelativePosition = rangeEncoderPosition(relativePosition);
                double relativePositionZeroDistance = relativePosition - rangedRelativePosition;

                double target = rangeEncoderPosition(requestedPosition + positionOffset) + relativePositionZeroDistance;

                if (target - relativePosition > maxRotationDistance) {
                    target -= countsPerRev;
                } else if (relativePosition - target > maxRotationDistance) {
                    target += countsPerRev;
                }

                talon.setHardware("position", target, profile);
            } else {
                talon.setHardware(outputType, outputValue, profile);
            }
        }
    }

    @Override
    public void processFlag(String flag) {
        if("zero".equals(flag)) {
           isZeroing = true;
        } else {
            talon.processFlag(flag);
        }
    }

    private double rangeEncoderPosition(double position) {
        return (((position % countsPerRev) - minAbsolutePosition + countsPerRev) % countsPerRev) + minAbsolutePosition;
    }
}
