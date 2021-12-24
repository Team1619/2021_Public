package org.uacr.purepursuit.path.heading;

import org.uacr.purepursuit.PathUtil;

public class LinearHeadingController extends HeadingController {

    private final double length;
    private final double initialHeading;
    private final double finalHeading;

    private LinearHeadingController(double length, double initialHeading, double finalHeading) {
        this.length = length;
        this.initialHeading = initialHeading;
        this.finalHeading = finalHeading;
    }

    @Override
    public Double getHeading(double currentDistance) {
        return PathUtil.interpolate(currentDistance, 0, length, initialHeading, finalHeading);
    }
}
