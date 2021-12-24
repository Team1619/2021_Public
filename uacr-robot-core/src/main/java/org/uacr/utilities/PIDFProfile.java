package org.uacr.utilities;

/**
 * PIDFProfile keeps track of PIDF values for motion magic
 *
 * @author Matthew Oates
 */

public class PIDFProfile {

    private final double p;
    private final double i;
    private final double d;
    private final double f;

    public PIDFProfile(double p, double i, double d, double f) {
        this.p = p;
        this.i = i;
        this.d = d;
        this.f = f;
    }

    public PIDFProfile(double p, double i, double d) {
        this(p, i, d, 0);
    }

    public PIDFProfile() {
        this(0, 0, 0);
    }

    /**
     * @return the P value for this profile
     */
    public double getP() {
        return p;
    }

    /**
     * @return the I value for this profile
     */
    public double getI() {
        return i;
    }

    /**
     * @return the D value for this profile
     */
    public double getD() {
        return d;
    }

    /**
     * @return the F value for this profile
     */
    public double getF() {
        return f;
    }

    /**
     * @return if the profile that is passed in has the same values as this profile
     */
    public boolean equals(PIDFProfile profile) {
        return p == profile.getP() && i == profile.i && d == profile.getD() && f == profile.getF();
    }
}
