package org.uacr.purepursuit.path.segment;

import org.uacr.purepursuit.math.Point;
import org.uacr.purepursuit.math.Pose2d;
import org.uacr.purepursuit.math.Vector;
import org.uacr.purepursuit.path.point.PathPoint;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PointSegment extends Segment {

    /**
     * Distance between each point (inches)
     */
    private double pointSpacing = 1;

    /**
     * The amount of smoothing to be done on the path (larger number = more smoothing)
     */
    private double pathSmoothing = 0.5;

    /**
     * Waypoints along path specified by behavior
     */
    private List<Point> points;

    /**
     * All the points along the path, created from the waypoints (points)
     */
    @Nullable
    private List<PathPoint> path;

    /**
     * Pass in an ArrayList of waypoints
     */
    public PointSegment(List<Point> points) {
        this.points = points;
    }

    /**
     * Pass in a comma separated list or array of waypoints
     */
    public PointSegment(Point... points) {
        this(new ArrayList<>(Arrays.asList(points)));
    }

    /**
     * Getters and Setters for path specific creation and following data
     */

    public double getPointSpacing() {
        return pointSpacing;
    }

    public void setPointSpacing(double pointSpacing) {
        this.pointSpacing = pointSpacing;
    }

    public double getPathSmoothing() {
        return pathSmoothing;
    }

    public void setPathSmoothing(double pathSmoothing) {
        this.pathSmoothing = pathSmoothing;
    }

    /**
     * Returns a single PathPoint from path
     *
     * @param index the index of the PathPoint
     * @return a PathPoint
     */
    public PathPoint getPoint(int index) {
        return getPathPoint(index);
    }

    /**
     * Returns all points in path (path).
     *
     * @return a PathPoint ArrayList
     */
    public List<PathPoint> getPoints() {
        if (path != null) {
            return new ArrayList<>(path);
        }
        build();
        return getPoints();
    }

    /**
     * Returns a single PathPoint from path
     *
     * @param point the index of the PathPoint
     * @return a PathPoint
     */
    private PathPoint getPathPoint(int point) {
        if (path != null) {
            return path.get(point);
        }
        build();
        return getPathPoint(point);
    }

    /**
     * Returns all points in path (path).
     *
     * @return a PathPoint ArrayList
     */
    private List<PathPoint> getPath() {
        if (path != null) {
            return path;
        }
        build();
        return getPath();
    }

    /**
     * Turns all the waypoints (points) into a path (path).
     */
    public void build() {

        if (path != null) {
            return;
        }

        if (points.size() == 0) {
            path = new ArrayList<>();
            return;
        }

        fill();

        smooth();
    }

    /**
     * Fills the spaces between waypoints (points) with a point pointSpacing inches.
     */
    private void fill() {
        ArrayList<Point> newPoints = new ArrayList<>();

        for (int s = 1; s < points.size(); s++) {
            Vector vector = new Vector(points.get(s - 1), points.get(s));

            int numPointsFit = (int) Math.ceil(vector.magnitude() / pointSpacing);

            vector = vector.normalize().scale(pointSpacing);

            for (int i = 0; i < numPointsFit; i++) {
                newPoints.add(points.get(s - 1).add(vector.scale(i)));
            }
        }

        newPoints.add(points.get(points.size() - 1));

        points = newPoints;
    }

    /**
     * Smooths the straight lines of points into a curved path.
     */
    private void smooth() {
        double change = 0.5;
        double changedPoints = 1;
        while (change / changedPoints >= 0.01) {
            change = 0;
            changedPoints = 0;

            List<Point> newPoints = new ArrayList<>(points);

            for (int i = 1; i < points.size() - 1; i++) {
                Point point = points.get(i);

                Vector middle = new Vector(points.get(i + 1).subtract(points.get(i - 1)));

                middle = new Vector(points.get(i - 1).add(middle.normalize().scale(middle.magnitude() / 2)));

                Vector delta = new Vector(middle.subtract(point));

                Point newPoint = point.add(delta.normalize().scale(delta.magnitude() * pathSmoothing));

                if (!Double.isNaN(newPoint.getX()) && !Double.isNaN(newPoint.getY())) {
                    newPoints.set(i, newPoint);
                    change += point.distance(newPoint);
                    changedPoints++;
                }
            }

            points = newPoints;
        }
    }

    @Override
    public double length() {
        return 0;
    }

    @Override
    public Point getLookaheadPoint(Pose2d currentPose) {
        return null;
    }

    @Override
    public double getDistance(Pose2d currentPose) {
        return 0;
    }

    @Override
    public boolean isDone(Pose2d currentPose) {
        return false;
    }

    @Override
    public double getInitialAngle() {
        return 0;
    }

    @Override
    public double getFinalAngle() {
        return 0;
    }

    @Override
    public Map<Double, Double> getSpeedReductions() {
        return null;
    }
}
