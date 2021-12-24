package org.uacr.purepursuit.math;

/**
 * Pose2d is an add on to the Point class,
 * allowing it to also store heading
 *
 * @author Matthew Oates
 */

public class Pose2d extends Point {

    private final double heading;

    public Pose2d(double x, double y, double heading) {
        super(x, y);

        this.heading = heading;
    }

    public Pose2d(Point point, double heading) {
        this(point.getX(), point.getY(), heading);
    }

    public Pose2d() {
        this(0, 0, 0);
    }

    public double getHeading() {
        return heading;
    }

    public Pose2d clone() {
        return new Pose2d(x, y, heading);
    }
}
