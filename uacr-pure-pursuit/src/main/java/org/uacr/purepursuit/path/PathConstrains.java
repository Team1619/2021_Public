package org.uacr.purepursuit.path;

public class PathConstrains {

    public double lookaheadDistance = 15;
    public double pointSegmentSmoothing = 0.5;
    public double maxVelocity = 1.0;
    public double minVelocity = 0.1;
    public double maxAcceleration = 0.01;
    public double maxDeceleration = 0.01;
    public double turnVelocityScalar = 0.5;
    public double velocityProfileResolution = 1.0;

    public PathConstrains copy() {
        try {
            return (PathConstrains) this.clone();
        } catch (CloneNotSupportedException e) {
            return new PathConstrains();
        }
    }
}
