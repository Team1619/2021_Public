package org.uacr.utilities.purepursuit;


import javax.annotation.Nullable;

public class PathDeviationConfig implements Cloneable {

    private final double start;
    private final double end;
    private final double startRamp;
    private final double endRamp;

    @Nullable
    private Double turnSpeed = null;
    @Nullable
    private Double maxAcceleration = null;
    @Nullable
    private Double maxDeceleration = null;
    @Nullable
    private Double minSpeed = null;
    @Nullable
    private Double maxSpeed = null;
    @Nullable
    private Double lookAheadDistance = null;
    @Nullable
    private Double velocityLookAheadPoints = null;

    public PathDeviationConfig(double start, double end, double startRamp, double endRamp) {
        this.start = start;
        this.end = end;
        this.startRamp = startRamp;
        this.endRamp = endRamp;
    }

    public PathDeviationConfig(double start, double end, double ramp) {
        this(start, end, ramp, ramp);
    }

    public PathDeviationConfig(double start, double end) {
        this(start, end, 0.0);
    }

    public double getStart() {
        return start;
    }

    public double getEnd() {
        return end;
    }

    public double getStartRamp() {
        return startRamp;
    }

    public double getEndRamp() {
        return endRamp;
    }

    public boolean hasTurnSpeed() {
        return null != turnSpeed;
    }
    
    public Double getTurnSpeed() {
        if(!hasTurnSpeed()) {
            throw new RuntimeException("Parameter doesn't have a value");
        }
        return turnSpeed;
    }

    public PathDeviationConfig setTurnSpeed(@Nullable Double turnSpeed) {
        this.turnSpeed = turnSpeed;
        return this;
    }

    public boolean hasMaxAcceleration() {
        return null != maxAcceleration;
    }

    public Double getMaxAcceleration() {
        if(!hasMaxAcceleration()) {
            throw new RuntimeException("Parameter doesn't have a value");
        }
        return maxAcceleration;
    }

    public PathDeviationConfig setMaxAcceleration(@Nullable Double maxAcceleration) {
        this.maxAcceleration = maxAcceleration;
        return this;
    }

    public boolean hasMaxDeceleration() {
        return null != maxDeceleration;
    }

    public Double getMaxDeceleration() {
        if(!hasMaxDeceleration()) {
            throw new RuntimeException("Parameter doesn't have a value");
        }
        return maxDeceleration;
    }

    public PathDeviationConfig setMaxDeceleration(@Nullable Double maxDeceleration) {
        this.maxDeceleration = maxDeceleration;
        return this;
    }

    public boolean hasMinSpeed() {
        return null != minSpeed;
    }

    public Double getMinSpeed() {
        if(!hasMinSpeed()) {
            throw new RuntimeException("Parameter doesn't have a value");
        }
        return minSpeed;
    }

    public PathDeviationConfig setMinSpeed(@Nullable Double minSpeed) {
        this.minSpeed = minSpeed;
        return this;
    }

    public boolean hasMaxSpeed() {
        return null != maxSpeed;
    }

    public Double getMaxSpeed() {
        if(!hasMaxSpeed()) {
            throw new RuntimeException("Parameter doesn't have a value");
        }
        return maxSpeed;
    }

    public PathDeviationConfig setMaxSpeed(@Nullable Double maxSpeed) {
        this.maxSpeed = maxSpeed;
        return this;
    }

    public boolean hasLookAheadDistance() {
        return null != lookAheadDistance;
    }

    public Double getLookAheadDistance() {
        if(!hasLookAheadDistance()) {
            throw new RuntimeException("Parameter doesn't have a value");
        }
        return lookAheadDistance;
    }
    
    public PathDeviationConfig setLookAheadDistance(@Nullable Double lookAheadDistance) {
        this.lookAheadDistance = lookAheadDistance;
        return this;
    }

    public boolean hasVelocityLookAheadPoints() {
        return null != velocityLookAheadPoints;
    }

    public Double getVelocityLookAheadPoints() {
        if(!hasVelocityLookAheadPoints()) {
            throw new RuntimeException("Parameter doesn't have a value");
        }
        return velocityLookAheadPoints;
    }

    public PathDeviationConfig setVelocityLookAheadPoints(@Nullable Double velocityLookAheadPoints) {
        this.velocityLookAheadPoints = velocityLookAheadPoints;
        return this;
    }

    public PathDeviationConfig clone() {
        PathDeviationConfig config = new PathDeviationConfig(start, end, startRamp, endRamp);

        config.setTurnSpeed(turnSpeed);
        config.setMaxAcceleration(maxAcceleration);
        config.setMaxDeceleration(maxDeceleration);
        config.setMinSpeed(minSpeed);
        config.setMaxSpeed(maxSpeed);
        config.setLookAheadDistance(lookAheadDistance);
        config.setVelocityLookAheadPoints(velocityLookAheadPoints);

        return config;
    }
}
