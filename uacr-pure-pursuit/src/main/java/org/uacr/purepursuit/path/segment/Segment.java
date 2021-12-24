package org.uacr.purepursuit.path.segment;

import org.uacr.purepursuit.math.Point;
import org.uacr.purepursuit.math.Pose2d;

import java.util.Map;

public abstract class Segment {

    private double lookaheadDistance;

    public Segment() {
        lookaheadDistance = 15;
    }

    public void setLookaheadDistance(double lookaheadDistance) {
        this.lookaheadDistance = lookaheadDistance;
    }

    public double getLookaheadDistance() {
        return lookaheadDistance;
    }

    /**
     * @return the total length of the segment.
     */
    public abstract double length();

    public abstract Point getLookaheadPoint(Pose2d currentPose);

    /**
     * Calculates the distance along the segment the given pose is
     *
     * @param currentPose the pose of the robot
     * @return the distance along the segment the robot is
     */
    public abstract double getDistance(Pose2d currentPose);

    /**
     * Determines whether the segment is done based on the robots pose
     *
     * @param currentPose the pose of the robot
     * @return whether the segment is finished
     */
    public abstract boolean isDone(Pose2d currentPose);

    public abstract double getInitialAngle();

    public abstract double getFinalAngle();

    /**
     * Calculates all the points of speed reduction along the segment.
     *
     * @return a map with the keys being distances of the speed reductions and values being the speed reductions.
     */
    public abstract Map<Double, Double> getSpeedReductions();
}
