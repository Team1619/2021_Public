package org.uacr.purepursuit.path;

import org.uacr.purepursuit.math.Point;
import org.uacr.purepursuit.path.segment.LineSegment;
import org.uacr.purepursuit.path.segment.PointSegment;
import org.uacr.purepursuit.path.segment.Segment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PathBuilder {

    public static PathBuilder start(Point point, PathConstrains constrains) {
        return new PathBuilder(point, constrains);
    }

    public static PathBuilder start(Point point) {
        return start(point, new PathConstrains());
    }

    public static PathBuilder start(double x, double y, PathConstrains constrains) {
        return start(new Point(x, y), constrains);
    }

    public static PathBuilder start(double x, double y) {
        return start(new Point(x, y));
    }

    private final PathConstrains defaultConstraints;
    private final List<Segment> segments;

    private SegmentBuildMode currentSegmentBuildMode;
    private Segment currentSegment;
    private List<Point> currentPoints;
    private Point currentPoint;

    private PathBuilder(Point point, PathConstrains constrains) {
        defaultConstraints = constrains;
        segments = new ArrayList<>();

        currentSegmentBuildMode = SegmentBuildMode.NONE;
        currentSegment = null;
        currentPoints = new ArrayList<>();
        currentPoint = point;
        currentPoints.add(currentPoint);
    }

    public PathBuilder lineTo(Point point) {
        if(currentSegmentBuildMode != SegmentBuildMode.LINE) {
            createSegment();
        }

        currentSegmentBuildMode = SegmentBuildMode.LINE;
        currentPoint = point;
        currentPoints.add(currentPoint);
        return this;
    }

    public PathBuilder lineTo(double x, double y) {
        return lineTo(new Point(x, y));
    }

    public PathBuilder pointsTo(List<Point> points) {
        createSegment();

        currentSegmentBuildMode = SegmentBuildMode.POINT;
        currentPoint = points.get(points.size() - 1);
        return this;
    }

    public PathBuilder pointsTo(Point... points) {
        return pointsTo(Arrays.asList(points));
    }

    private void createSegment() {
        if(currentSegmentBuildMode == SegmentBuildMode.NONE || currentPoints.size() < 1) {
            return;
        }

        switch (currentSegmentBuildMode) {
            case LINE:
                segments.add(new LineSegment(currentPoints));
                break;
            case POINT:
                segments.add(new PointSegment(currentPoints));
                break;
        }

        currentPoints.clear();
    }

    public Path build() {
        createSegment();

        return Path.createCompoundPath(segments, defaultConstraints);
    }

    private enum SegmentBuildMode {
        NONE,
        LINE,
        POINT
    }
}
