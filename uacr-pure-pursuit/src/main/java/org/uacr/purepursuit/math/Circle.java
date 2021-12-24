package org.uacr.purepursuit.math;

import java.util.ArrayList;
import java.util.List;

public class Circle {

    private final Point center;
    private final double radius;

    public Circle(Point center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    public Circle(double x, double y, double radius) {
        this(new Point(x, y), radius);
    }

    public Point getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }

    public double getDiameter() {
        return radius * 2;
    }

    public double getCircumference() {
        return getDiameter() * Math.PI;
    }

    /**
     * Finds all intersections between the circle and the given line
     * Equation from: https://mathworld.wolfram.com/Circle-LineIntersection.html
     *
     * @param line to find intersections with the circle
     * @return a list of intersections
     */
    public List<Point> getIntersections(Line line) {
        List<Point> intersections = new ArrayList<>();

        Vector lineShift = new Vector(center).invert();

        Line shiftedLine = line.shift(lineShift);

        double d = shiftedLine.initial().getX() * shiftedLine.terminal().getY() - shiftedLine.terminal().getX() * shiftedLine.initial().getY();
        double deltaX = shiftedLine.delta().getX();
        double deltaY = shiftedLine.delta().getY();
        double deltaMagnitude = shiftedLine.delta().magnitude();
        double discriminant = Math.pow(getRadius(), 2) * Math.pow(deltaMagnitude, 2) - Math.pow(d, 2);

        if (discriminant < 0) {
            return intersections;
        }

        double xDiscriminant = (deltaY < 0 ? -1 : 1) * deltaX * Math.sqrt(discriminant);
        double yDiscriminant = Math.abs(deltaY) * Math.sqrt(discriminant);

        intersections.add(new Point((d * deltaY + xDiscriminant) / Math.pow(deltaMagnitude, 2),
                (-d * deltaX + yDiscriminant) / Math.pow(deltaMagnitude, 2)));

        if (discriminant > 1) {
            intersections.add(new Point((d * deltaY - xDiscriminant) / Math.pow(deltaMagnitude, 2),
                    (-d * deltaX - yDiscriminant) / Math.pow(deltaMagnitude, 2)));
        }

        intersections.replaceAll(p -> p.subtract(lineShift));

        return intersections;
    }

    public String toString() {
        return "Center: " + getCenter() + " Radius: " + getRadius();
    }
}
