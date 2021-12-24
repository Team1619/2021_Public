package org.uacr.utilities.purepursuit;

import java.util.List;
import java.util.Objects;

/**
 * Vector is an add on to the Point class,
 * allowing it to preform vector operations
 *
 * @author Matthew Oates
 */

public class Vector extends Point {

    private double magnitude;
    private double angle;

    public Vector() {
        super();
        this.magnitude = 0;
        this.angle = 0;
    }

    public Vector(double magnitude, double angle) {
        this(new Point(magnitude * Math.cos(Math.toRadians(angle)), magnitude * Math.sin(Math.toRadians(angle))));
        this.magnitude = magnitude;
        this.angle = angle;
    }

    public Vector(List<Double> coordinates) {
        super(coordinates);
        this.magnitude = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        this.angle = Math.toDegrees(Math.atan2(y, x));
    }

    public Vector(Point point) {
        super(point.getX(), point.getY());
        this.magnitude = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        this.angle = Math.toDegrees(Math.atan2(y, x));
    }

    public Vector(double x1, double y1, double x2, double y2) {
        this(new Point(x2 - x1, y2 - y1));
        this.magnitude = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        this.angle = Math.toDegrees(Math.atan2(y, x));
    }

    public Vector(Point point1, Point point2) {
        this(point1.getX(), point1.getY(), point2.getX(), point2.getY());
    }

    public double magnitude() {
        return magnitude;
    }

    public double angle() {
        return angle;
    }

    public Vector normalize() {
        return new Vector(1, angle());
    }

    public Vector scale(double scalar) {
        return new Vector(magnitude() * scalar, angle());
    }

    public Vector rotate(double degrees) {
        return new Vector(magnitude(), angle() + degrees);
    }

    public double dot(Vector vector) {
        return x * vector.getX() + y * vector.getY();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Vector vector = (Vector) o;
        return Double.compare(vector.magnitude, magnitude) == 0 && Double.compare(vector.angle, angle) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), magnitude, angle);
    }

    public String toString() {
        return "<" + x + "," + y + ">";
    }
}
