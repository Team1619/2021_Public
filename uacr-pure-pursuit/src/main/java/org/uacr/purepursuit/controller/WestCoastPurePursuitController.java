package org.uacr.purepursuit.controller;

import org.uacr.purepursuit.math.Point;
import org.uacr.purepursuit.math.Pose2d;
import org.uacr.purepursuit.math.Vector;
import org.uacr.purepursuit.path.Path;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

import javax.annotation.Nullable;

public abstract class WestCoastPurePursuitController extends PurePursuitController {

    private static final Logger logger = LogManager.getLogger(WestCoastPurePursuitController.class);

    private final double trackWidth;

    @Nullable
    private Path currentPath;
    private Pose2d currentPose;
    private Pose2d followPose;
    private FollowDirection followDirection;
    private boolean isFollowing;
    private double deltaAngle;

    public WestCoastPurePursuitController(double trackWidth) {
        this.trackWidth = trackWidth;

        currentPath = null;
        currentPose = new Pose2d();
        followPose = new Pose2d();
        followDirection = FollowDirection.FORWARD;
        isFollowing = false;
        deltaAngle = 0;
    }

    public double getTrackWidth() {
        return trackWidth;
    }

    public void followPath(Path path) {
        currentPath = path;
        currentPath.reset();
        resetFollower();
        isFollowing = true;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public boolean isPathFinished() {
        return !isFollowing();
    }

    public FollowDirection getFollowDirection() {
        return followDirection;
    }

    public void setFollowDirection(FollowDirection followDirection) {
        this.followDirection = followDirection;
    }

    public void resetFollower() {
        currentPose = new Pose2d();
        followPose = new Pose2d();
    }

    public void updateFollower() {
        if (currentPath == null || !isFollowing) {
            stopDrive();

            return;
        }

        currentPose = getCurrentPose();

        followPose = currentPose.clone();

        if (followDirection == FollowDirection.REVERSE) {
            followPose = new Pose2d(followPose.getX(), followPose.getY(), ((followPose.getHeading() + 360) % 360) - 180);
        }

        // Uses the path object to calculate curvature and velocity values
        double velocity = currentPath.getVelocity(followPose);
        Point lookaheadPoint = currentPath.getLookaheadPoint(followPose);

        logger.info("Length: {} - Current Pose: {} - Lookahead: {} - Velocity: {}", currentPath.length(), followPose, lookaheadPoint, velocity);

        updateDriveVelocities(velocity, getCurvature(currentPose, lookaheadPoint));

        if(currentPath.isDone(followPose)) {
            isFollowing = false;
        }
    }

    protected void updateDriveVelocities(double velocity, double curvature) {
        if (followDirection == FollowDirection.REVERSE) {
            setDriveVelocities(-(velocity * ((1.5 - curvature * trackWidth) / 1.5)),
                    -(velocity * ((1.5 + curvature * trackWidth) / 1.5)));
        } else {
            setDriveVelocities(velocity * ((1.5 + curvature * trackWidth) / 1.5),
                    velocity * ((1.5 - curvature * trackWidth) / 1.5));
        }
    }

    protected double getCurvature(Pose2d currentPosition, Point point) {
        Vector delta = new Vector(point.subtract(currentPosition));

        double angle = Math.toDegrees(Math.atan2(delta.getY(), Math.abs(delta.getX()) > 0.3 ? delta.getX() : 0.3 * Math.signum(delta.getX())));

        deltaAngle = currentPosition.getHeading() - angle;

        if (Math.abs(deltaAngle) > 180) deltaAngle = -Math.signum(deltaAngle) * (360 - Math.abs(deltaAngle));

        double curvature = (Math.abs(deltaAngle) > 90 ? Math.signum(deltaAngle) : Math.sin(Math.toRadians(deltaAngle))) / (delta.magnitude() / 2);

        if (Double.isInfinite(curvature) || Double.isNaN(curvature)) return 0.0;

        return curvature;
    }

    public void stopDrive() {
        setDriveVelocities(0.0, 0.0);
    }

    public abstract void setDriveVelocities(double leftVelocity, double rightVelocity);

    public abstract Pose2d getCurrentPose();

    public enum FollowDirection {
        FORWARD,
        REVERSE
    }
}
