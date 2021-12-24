package org.uacr.utilities.purepursuit.spline;

import org.uacr.utilities.purepursuit.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Class describes a spline curve created parametrically. Allows for the creation of spline paths that aren't functions.
 */
public class ParametricSpline {
    private final Spline splineX;
    private final Spline splineY;


    // Constructors create splines of x and y as functions of some parameter t (index of the point)
    public ParametricSpline(List<? extends Point> points, double startingHeading, double endingHeading) {
        double[] t = new double[points.size()];
        double[] x = new double[points.size()];
        double[] y = new double[points.size()];
        for (int i = 0; i < points.size(); i++) {
            t[i] = i;
            x[i] = points.get(i).getX();
            y[i] = points.get(i).getY();
        }

        splineX = new Spline(t, x, 45, 45);
        splineY = new Spline(t, y, startingHeading, endingHeading);
    }

    public ParametricSpline(List<? extends Point> points) {
        double[] t = new double[points.size()];
        double[] x = new double[points.size()];
        double[] y = new double[points.size()];
        for (int i = 0; i < points.size(); i++) {
            t[i] = i;
            x[i] = points.get(i).getX();
            y[i] = points.get(i).getY();
        }

        splineX = new Spline(t, x);
        splineY = new Spline(t, y);
    }

    /**
     * Returns the set of points on the parametric spline that are spaced a certain distance (Euclidean) apart.
     *
     * This is where the spline interpolation is actually used to generate points not originally in the waypoints.
     */
    public ArrayList<Point> getPoints(double spacing) {
        ArrayList<Point> result = new ArrayList<>();
        double length = 0;
        double target = 0;
        final double k = 0.001;
        // Using Riemann sum to approximate the parametric arc length integral
        for (double t = 0; t < splineX.getUpperBound(); t += k) {
            length += getDerivativeOfArcLength(t) * k;
            // Once the length traveled is twice spacing, add these values as a point in the output
            if (length >= target) {
                result.add(new Point(splineX.eval(t), splineY.eval(t)));
                target += spacing * 2;
            }
        }
        // Add the ending point, since it won't be added in the for loop
        result.add(new Point(splineX.eval(splineX.getUpperBound()), splineY.eval(splineX.getUpperBound())));
        return result;
    }

    /**
     * Returns the derivative of the arc length of the spline between its start and at a certain t-coordinate.
     *
     * Used to ensure spacing is even in the getPoints() method.
     */
    private double getDerivativeOfArcLength(double t) {
        Polynomial xDerivative = splineX.getCurve(t).getDerivative();
        Polynomial yDerivative = splineY.getCurve(t).getDerivative();
        return Math.sqrt(Math.pow(xDerivative.eval(t), 2) + Math.pow(yDerivative.eval(t), 2));
    }
}
