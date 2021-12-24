package org.uacr.purepursuit.path;

import org.uacr.purepursuit.PathUtil;
import org.uacr.purepursuit.math.Point;
import org.uacr.purepursuit.math.Pose2d;
import org.uacr.purepursuit.path.segment.Segment;
import org.uacr.purepursuit.path.velocity.TrapezoidVelocityProfile;
import org.uacr.purepursuit.path.velocity.VelocityProfile;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Path {

    public static Path createLinePath() {
        return new Path();
    }

    public static Path createPointPath() {
        return new Path();
    }

    public static Path createCompoundPath(List<Segment> segments, PathConstrains constrains) {
        return new Path(segments, constrains);
    }

    public static Path createCompoundPath(List<Segment> segments) {
        return new Path(segments);
    }

    private final PathConstrains constraints;

    private final List<Segment> segments;
    private final VelocityProfile profile;

    private int segmentIndex;

    private Path(List<Segment> segments, PathConstrains constraints) {
        this.constraints = constraints;

        this.segments = segments;
        segmentIndex = 0;

        profile = new TrapezoidVelocityProfile(this.constraints, length(), getSpeedReductions());
    }

    private Path(List<Segment> segments) {
        this(segments, new PathConstrains());
    }

    private Path(Segment... segments) {
        this(Arrays.asList(segments));
    }

    public double length() {
        return segments.stream().mapToDouble(Segment::length).sum();
    }

    public Point getLookaheadPoint(Pose2d currentPosition) {
        return updateCurrentSegment(currentPosition).getLookaheadPoint(currentPosition);
    }

    public double getVelocity(Pose2d currentPosition) {
        return profile.getVelocity(updateCurrentSegment(currentPosition).getDistance(currentPosition));
    }

    public double getHeading(Pose2d currentPosition) {
        return 0.0;
    }

    private Segment updateCurrentSegment(Pose2d currentPosition) {
        Segment currentSegment = segments.get(segmentIndex);

        if (segmentIndex != segments.size() - 1 && currentSegment.isDone(currentPosition)) {
            segmentIndex++;
            currentSegment = segments.get(segmentIndex);
        }

        currentSegment.setLookaheadDistance(constraints.lookaheadDistance);
        return currentSegment;
    }

    public boolean isDone(Pose2d currentPosition) {
        return updateCurrentSegment(currentPosition).isDone(currentPosition);
    }

    public Map<Double, Double> getSpeedReductions() {
        Map<Double, Double> speedReductions = new HashMap<>();

        double distance = 0.0;

        for(int s = 0; s < segments.size(); s++) {
            Segment segment = segments.get(s);

            for(Map.Entry<Double, Double> segmentSpeedReduction : segment.getSpeedReductions().entrySet()) {
                speedReductions.put(segmentSpeedReduction.getKey() + distance, segmentSpeedReduction.getValue());
            }

            distance += segment.length();

            if(s < segments.size() - 1) {
                double speedReduction = Math.abs(PathUtil.angleWrap(segments.get(s + 1).getInitialAngle() - segment.getFinalAngle())) / 90;

                if (0 < speedReduction && speedReduction <= 1) {
                    speedReductions.put(distance, speedReduction);
                }
            }
        }

        return speedReductions;
    }

    public void reset() {

    }

    public String toString() {
        return segments.toString();
    }
}
