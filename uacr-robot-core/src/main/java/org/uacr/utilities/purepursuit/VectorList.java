package org.uacr.utilities.purepursuit;

import org.uacr.utilities.Lists;

import java.util.ArrayList;
import java.util.List;

public class VectorList extends ArrayList<Vector> {

    public VectorList(Vector... vectors) {
        super(Lists.of(vectors));
    }

    public VectorList(List<Vector> vectors) {
        super(vectors);
    }

    public VectorList addAll(Point point) {
        replaceAll(v -> new Vector(v.add(point)));

        return this;
    }

    public VectorList subtractAll(Point point) {
        replaceAll(v -> new Vector(v.subtract(point)));

        return this;
    }

    public VectorList normalizeAll() {
        replaceAll(v -> v.normalize());

        return this;
    }

    public VectorList scaleAll(double scalar) {
        replaceAll(v -> v.scale(scalar));

        return this;
    }

    public VectorList rotateAll(double degrees) {
        replaceAll(v -> v.rotate(degrees));

        return this;
    }

    public VectorList autoScaleAll(AutoScaleMode mode, double value) {
        double scaleValue;

        if(mode.largest()) {
            scaleValue = maxMagnitude();
        } else {
            scaleValue = minMagnitude();
        }

        double scalar = 1;

        if((mode.up() && value > scaleValue) || (mode.down() && value < scaleValue)) {
            scalar = value / scaleValue;
        }

        scaleAll(scalar);

        return this;
    }

    public double maxMagnitude() {
        double maxMagnitude = 0.0;

        for (Vector v : this) {
            maxMagnitude = Math.max(maxMagnitude, Math.abs(v.magnitude()));
        }

        return maxMagnitude;
    }

    public double minMagnitude() {
        double minMagnitude = Double.MAX_VALUE;

        for (Vector v : this) {
            minMagnitude = Math.min(minMagnitude, Math.abs(v.magnitude()));
        }

        return minMagnitude;
    }

    public VectorList copy() {
        return new VectorList(this);
    }

    public enum AutoScaleMode {
        SCALE_LARGEST_UP(true, true, false),
        SCALE_LARGEST_DOWN(true, false, true),
        SCALE_SMALLEST_UP(false, true, false),
        SCALE_SMALLEST_DOWN(false, false, false),
        SCALE_LARGEST_UP_OR_DOWN(true, true, true),
        SCALE_SMALLEST_UP_OR_DOWN(false, true, true);

        private final boolean largest;
        private final boolean up;
        private final boolean down;

        private AutoScaleMode(boolean largest, boolean up, boolean down) {
            this.largest = largest;
            this.up = up;
            this.down = down;
        }

        public boolean largest() {
            return largest;
        }

        public boolean smallest() {
            return !largest;
        }

        public boolean up() {
            return up;
        }

        public boolean down() {
            return down;
        }
    }
}
