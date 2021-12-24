package org.uacr.purepursuit.path.velocity;

import org.uacr.purepursuit.PathUtil;
import org.uacr.purepursuit.math.Line;
import org.uacr.purepursuit.math.Point;
import org.uacr.purepursuit.math.Vector;
import org.uacr.purepursuit.path.PathConstrains;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TrapezoidVelocityProfile implements VelocityProfile {

    public final List<Line> velocityProfile;

    public TrapezoidVelocityProfile(PathConstrains constrains, double length, Map<Double, Double> speedReductions) {
        velocityProfile = new ArrayList<>();

        Line minVelocityLine = new Line(new Point(0, constrains.minVelocity), new Vector(length, 0));
        Line maxVelocityLine = new Line(new Point(0, constrains.maxVelocity), new Vector(length, 0));

        velocityProfile.add(new Line(minVelocityLine.initial(), new Line(minVelocityLine.initial(), minVelocityLine.initial().add(new Point(1, constrains.maxAcceleration))).intersection(maxVelocityLine)));

        for(double reductionDistance : speedReductions.keySet().stream().sorted().collect(Collectors.toList())) {
            Point speedReductionPoint = new Point(reductionDistance, PathUtil.interpolate(speedReductions.get(reductionDistance), 0, 1, constrains.maxVelocity, constrains.minVelocity));

            velocityProfile.add(new Line(new Line(speedReductionPoint, speedReductionPoint.add(new Point(-1, constrains.maxDeceleration))).intersection(maxVelocityLine), speedReductionPoint));
            velocityProfile.add(new Line(speedReductionPoint, new Line(speedReductionPoint, speedReductionPoint.add(new Point(1, constrains.maxAcceleration))).intersection(maxVelocityLine)));
        }

        velocityProfile.add(new Line(new Line(minVelocityLine.terminal(), minVelocityLine.terminal().add(new Point(-1, constrains.maxDeceleration))).intersection(maxVelocityLine), minVelocityLine.terminal()));

        for(int l = 0; l < velocityProfile.size() - 1; l++) {
            if(PathUtil.toleranceEquals(Math.abs(velocityProfile.get(l).slope()), 0, 0.00001)) {
                continue;
            }

            while (true) {
                Point nextLineIntersection = velocityProfile.get(l).intersection(velocityProfile.get(l + 1));

                if (nextLineIntersection.getY() > constrains.maxVelocity + 0.0001) {
                    velocityProfile.add(l + 1, new Line(velocityProfile.get(l).intersection(maxVelocityLine),
                            velocityProfile.get(l + 1).intersection(maxVelocityLine)));
                    break;
                } else if (velocityProfile.get(l).slope() > 0.0) {
                    if (velocityProfile.get(l + 1).isInSegment(nextLineIntersection)) {
                        velocityProfile.set(l, new Line(velocityProfile.get(l).initial(), nextLineIntersection));
                        velocityProfile.set(l + 1, new Line(nextLineIntersection, velocityProfile.get(l + 1).terminal()));
                        break;
                    }
                    velocityProfile.remove(l + 1);
                    velocityProfile.remove(l + 1);
                } else {
                    break;
                }
            }
        }
    }


    public double getVelocity(double distance) {
        for(Line line : velocityProfile) {
            if(line.isInDomain(distance)) {
                return line.evaluateX(distance).getY();
            }
        }
        if(distance < velocityProfile.get(0).initial().getX()) {
            return velocityProfile.get(0).initial().getY();
        }
        if(velocityProfile.get(velocityProfile.size() - 1).terminal().getX() < distance) {
            return velocityProfile.get(velocityProfile.size() - 1).terminal().getY();
        }

        return -1;
    }
}
