package org.uacr.purepursuit.math;

import org.uacr.purepursuit.PathUtil;

public class Line {

    private final Point initial;
    private final Point terminal;
    private final Vector delta;

    public Line(Point initial, Point terminal) {
        this.initial = initial;
        this.terminal = terminal;
        delta = new Vector(terminal.subtract(initial));
    }

    public Line(Point initial, Vector terminalFromInitial) {
        this(initial, initial.add(terminalFromInitial));
    }

    public Point initial() {
        return initial;
    }

    public Point terminal() {
        return terminal;
    }

    public Vector delta() {
        return delta;
    }

    public double slope() {
        return delta().getY() / delta().getX();
    }

    public double angle() {
        return delta().angle();
    }

    public Point midpoint() {
        return initial().add(delta().scale(0.5));
    }

    public double length() {
        return delta().magnitude();
    }

    public Line shift(Vector vector) {
        return new Line(initial().add(vector), terminal().add(vector));
    }

    public Point evaluateX(double x) {
        return new Point(x, initial().getY() + (x - initial().getX()) * slope());
    }

    public Point evaluateY(double y) {
        return new Point(initial().getX() + (y - initial().getY()) / slope(), y);
    }

    public Point intersection(Line line) {
        if (slope() == line.slope()) {
            return null;
        }

        if (slope() == Double.POSITIVE_INFINITY || slope() == Double.NEGATIVE_INFINITY) {
            return line.evaluateX(initial().getX());
        }

        if (line.slope() == Double.POSITIVE_INFINITY || line.slope() == Double.NEGATIVE_INFINITY) {
            return evaluateX(line.initial().getX());
        }

        return evaluateX((line.evaluateX(0).getY() - evaluateX(0).getY()) / (slope() - line.slope()));
    }

    public Point pointFromDistance(double distance) {
        return initial().add(delta().normalize().scale(distance));
    }

    public Point closestPoint(Point point) {
        return intersection(new Line(point, point.add(new Vector(1, angle()).rotate(90))));
    }

    public boolean isInSegment(Point point) {
        Point closestPoint = closestPoint(point);


        return (PathUtil.toleranceEquals(new Vector(closestPoint.subtract(terminal())).angle(), new Vector(initial().subtract(terminal())).angle(), 0.00001) &&
                PathUtil.toleranceEquals(new Vector(closestPoint.subtract(initial())).angle(), delta().angle(), 0.00001)) ||
                point.equals(initial()) || point.equals(terminal());
    }

    public boolean isInDomain(double x) {
        return Math.min(initial().getX(), terminal().getX()) <= x && x <= Math.max(initial().getX(), terminal().getX());
    }

    public boolean isInRange(double y) {
        return Math.min(initial().getY(), terminal().getY()) <= y && y <= Math.max(initial().getY(), terminal().getY());
    }

    public Point closestPointInSection(Point point) {
        Point closestPoint = closestPoint(point);
        if (new Vector(closestPoint.subtract(initial())).angle() != delta().angle()) {
            closestPoint = initial();
        }
        if (new Vector(closestPoint.subtract(terminal())).angle() != new Vector(initial().subtract(terminal())).angle()) {
            closestPoint = terminal();
        }
        return closestPoint;
    }

    public double distanceFromInitial(Point point) {
        return new Vector(point.subtract(initial())).magnitude();
    }

    public double distanceFromTerminal(Point point) {
        return new Vector(point.subtract(terminal())).magnitude();
    }

    public String toString() {
        return "Initial: " + initial() + " Terminal: " + terminal();
    }
}
