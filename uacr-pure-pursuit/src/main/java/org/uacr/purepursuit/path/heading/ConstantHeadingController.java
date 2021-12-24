package org.uacr.purepursuit.path.heading;

public class ConstantHeadingController extends HeadingController {

    private final double angle;

    public ConstantHeadingController(double angle) {
        this.angle = angle;
    }

    @Override
    public Double getHeading(double currentDistance) {
        return angle;
    }
}
