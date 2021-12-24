package org.uacr.purepursuit.path.segment;

import org.uacr.purepursuit.PathUtil;
import org.uacr.purepursuit.math.Line;
import org.uacr.purepursuit.math.Point;
import org.uacr.purepursuit.math.Pose2d;
import org.uacr.purepursuit.math.Vector;

import java.util.*;

public class LineSegment extends Segment {

    private final List<Line> lines;

    private int currentLineIndex;
    private boolean isDone;

    public LineSegment(List<Point> points) {
        lines = new ArrayList<>();

        for (int p = 0; p < points.size() - 1; p++) {
            lines.add(new Line(points.get(p), points.get(p + 1)));
        }

        currentLineIndex = 0;
        isDone = false;
    }

    public LineSegment(Point... points) {
        this(Arrays.asList(points));
    }

    public Point getLookaheadPoint(Pose2d currentPose) {
        if (lines.size() < 1 || currentLineIndex > lines.size()) {
            return null;
        }

        Point lookaheadPoint = null;

        while (currentLineIndex < lines.size()) {
            lookaheadPoint = getCorrectIntersection(currentPose, lines.get(currentLineIndex));
            if (lookaheadPoint != null) {
                break;
            }
            if (currentLineIndex >= lines.size() - 1) {
                break;
            }
            currentLineIndex++;
        }

        if (lookaheadPoint == null) {
            lookaheadPoint = lines.get(lines.size() - 1).terminal();
        }

        return lookaheadPoint;
    }

    @Override
    public double getDistance(Pose2d currentPose) {
        double distance = 0.0;

        for (int l = currentLineIndex - 1; l < currentLineIndex; l++) {
            distance += lines.get(currentLineIndex).length();
        }

        return distance + lines.get(currentLineIndex).distanceFromInitial(lines.get(currentLineIndex).closestPointInSection(currentPose));
    }

    private Point getCorrectIntersection(Pose2d currentPose, Line line) {
        Point correctIntersection = null;

        Point closestPoint = line.closestPoint(currentPose);
        double closestPointDistance = closestPoint.distance(currentPose);

        // If the distance from the robot to the closest point on the line is greater than the lookahead distance,
        // then use the closest point on the line as the intersection
        if (closestPointDistance >= getLookaheadDistance()) {
            correctIntersection = closestPoint;
        } else {
            // Calculate the distance from the closest point to the intersection with the pythagorean theorem
            double lineIntersectionDistance = Math.sqrt(Math.pow(getLookaheadDistance(), 2) - Math.pow(closestPointDistance, 2));

            // Project the distance of the intersection from the current point in the direction of the line segment
            correctIntersection = closestPoint.add(new Vector(lineIntersectionDistance, line.angle()));
        }

        // If the intersection point exists but is beyond the end of the line segment,
        // set it too null to move to the next segment
        if (Math.abs(PathUtil.angleDifference(line.angle(),
                        new Vector(line.terminal().subtract(correctIntersection)).angle())) > 0.1) {
            // If the angle from the terminal point of the line segment to the intersection is the same as the angle
            // of the line then the intersection is beyond the end of the line

            correctIntersection = null;
        }

        return correctIntersection;
    }

    public double length() {
        return lines.stream().mapToDouble(Line::length).sum();
    }

    @Override
    public boolean isDone(Pose2d currentPose) {
        return isDone;
    }

    @Override
    public double getInitialAngle() {
        return lines.get(0).delta().angle();
    }

    @Override
    public double getFinalAngle() {
        return lines.get(lines.size() - 1).delta().angle();
    }

    @Override
    public Map<Double, Double> getSpeedReductions() {
        Map<Double, Double> speedReductions = new HashMap<>();

        double distance = 0.0;

        for (int p = 0; p < lines.size() - 1; p++) {
            distance += lines.get(p).length();

            // Calculate the amount of speed reduction
            // A 90 degree angle or greater will cause a full speed reduction to the minimum path speed
            double speedReduction = Math.abs(PathUtil.angleWrap(lines.get(p + 1).delta().angle() - lines.get(p).delta().angle())) / 90;

            // If there is a speed reduction at the current point put it in the map
            if (0 < speedReduction) {
                speedReductions.put(distance, Math.min(speedReduction, 1));
            }
        }

        return speedReductions;
    }

    public String toString() {
        return lines.toString();
    }
}
