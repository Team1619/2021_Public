package org.uacr.utilities.closedloopcontroller;

import org.uacr.models.exceptions.ConfigurationException;

import java.util.Map;

public class ClosedLoopControllerProfile {

    private final String name;

    private final double f;
    private final double p;
    private final double i;
    private final double d;
    private final double maxIntegral;
    private final double integralRange;
    private final double maxOutput;
    private final double idleOutput;

    private double kv;
    private double ka;
    private double kForceCompensation;
    private boolean hasFeedForward;
    private boolean hasForceCompensation;

    public ClosedLoopControllerProfile(String name, Map<String, Double> values) {
        this.name = name;
        if (values.containsKey("f") && values.containsKey("p") && values.containsKey("i") && values.containsKey("d") &&
                values.containsKey("max_integral") && values.containsKey("integral_range") && values.containsKey("max_output") && values.containsKey("idle_output")) {
            f = values.get("f");
            p = values.get("p");
            i = values.get("i");
            d = values.get("d");

            maxIntegral = values.get("max_integral");
            integralRange = values.get("integral_range");
            maxOutput = values.get("max_output");
            idleOutput = values.get("idle_output");

            if (values.containsKey("ka") && values.containsKey("kv")) {
                hasFeedForward = true;
                ka = values.get("ka");
                kv = values.get("kv");
            }

            if (values.containsKey("force_compensation")) {
                hasForceCompensation = true;
                kForceCompensation = values.get("force_compensation");

            }
        } else {
            throw new ConfigurationException("Must provide value for 'f', 'p', 'i', 'd', 'max_output'");
        }
    }

    public double getF() {
        return f;
    }

    public double getP() {
        return p;
    }

    public double getI() {
        return i;
    }

    public double getD() {
        return d;
    }

    public double getMaxIntegral() {
        return maxIntegral;
    }

    public double getIntegralRange() {
        return integralRange;
    }

    public double getMaxOutput() {
        return maxOutput;
    }

    public double getIdleOutput() {
        return idleOutput;
    }

    public double getKv() {
        return kv;
    }

    public double getka() {
        return ka;
    }

    public double getKForceCompensation() {
        return kForceCompensation;
    }

    public boolean hasFeedForward() {
        return hasFeedForward;
    }

    public boolean hasForceCompensation() {
        return hasForceCompensation;
    }
}
