package org.uacr.utilities.purepursuit.spline;

/**
 * Class to represent a polynomial and do computations with it.
 */
public class Polynomial {
    private final int order;
    // coefficients[i] is the coefficient for the x^i term
    private final double[] coefficients;

    // For splines, the lower and upper points where this polynomial should be used
    private final double lowerBound;
    private final double upperBound;
    // The exponential terms are of the form (x - offset)^n
    private final double offset;

    public Polynomial(int order, double lowerBound, double upperBound, double offset, double... coefficients) {
        if (coefficients.length != order + 1) {
            throw new IllegalArgumentException("Polynomial must have the same number of coefficients as its order");
        }
        this.order = order;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.offset = offset;
        this.coefficients = coefficients;
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public double getUpperBound() {
        return upperBound;
    }

    /**
     * Returns the value of the polynomial at a certain x-coordinate.
     *
     * Note that this does not check if it's within the bounds of the polynomial for the purpose of spline calculation.
     * That must be checked elsewhere.
     */
    public double eval(double x) {
        double result = 0;
        for (int i = 0; i < coefficients.length; i++) {
            result += coefficients[i] * Math.pow(x - offset, i);
        }
        return result;
    }

    /**
     * Returns the derivative of the polynomial (as another Polynomial object).
     */
    public Polynomial getDerivative() {
        double[] newCoefficients = new double[order];
        for (int i = 1; i < coefficients.length; i++) {
            // Power rule
            newCoefficients[i - 1] = coefficients[i] * (i + 1);
        }
        return new Polynomial(order - 1, lowerBound, upperBound, offset, newCoefficients);
    }

    public String toString() {
        String result = "Polynomial of order " + order + ": ";
        for (int i = 0; i < coefficients.length; i++) {
            result += coefficients[i] + "(x-" + offset + ")^" + i + "+ ";
        }
        return result + "\b\b";
    }
}