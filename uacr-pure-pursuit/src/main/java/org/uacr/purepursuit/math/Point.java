package org.uacr.purepursuit.math;

import java.util.List;

/**
 * Point is a simple class which stores an x and y value for a point,
 * and can do simple operations on the point
 *
 * @author Matthew Oates
 */

public class Point {

    protected final double x;
    protected final double y;

    public Point() {
        x = 0;
        y = 0;
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point(List<Double> coordinates) {
        if (coordinates.size() < 1) {
            x = 0;
            y = 0;
        } else if (coordinates.size() < 2) {
            x = coordinates.get(0);
            y = 0;
        } else {
            x = coordinates.get(0);
            y = coordinates.get(1);
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Point add(Point point) {
        return new Point(x + point.getX(), y + point.getY());
    }

    public Point subtract(Point point) {
        return new Point(x - point.getX(), y - point.getY());
    }

    public double distance(Point point) {
        return Math.sqrt(Math.pow(point.x - x, 2) + Math.pow(point.y - y, 2));
    }

    public boolean equals(Object object) {
        if (object instanceof Point) {
            Point point = (Point) object;

            if (getX() == point.getX() && getY() == point.getY()) {
                return true;
            }
        }

        return false;
    }

    public String toString() {
        return "(" + String.format("%.4f", x) + ", " + String.format("%.4f", y) + ")";
    }
}
