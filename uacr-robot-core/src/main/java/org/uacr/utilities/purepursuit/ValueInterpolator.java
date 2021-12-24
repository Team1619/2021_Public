package org.uacr.utilities.purepursuit;

import java.util.*;
import java.util.stream.Collectors;

public class ValueInterpolator {

    private final double defaultValue;
    private final List<FullValueDeviation> deviations;

    public ValueInterpolator(double defaultValue, List<ValueDeviation> deviations) {
        this.defaultValue = defaultValue;
        if (deviations.stream().mapToDouble(ValueDeviation::getStart).distinct().toArray().length < deviations.size()) {
            throw new RuntimeException("ValueDeviations must not have the same starting location");
        }
        deviations = deviations.stream().sorted(Comparator.comparingDouble(ValueDeviation::getStart)).collect(Collectors.toList());


        this.deviations = new ArrayList<>();

        for (int d = 0; d < deviations.size(); ) {
            ValueDeviation deviation = deviations.get(d);

            while (true) {
                if (d >= deviations.size()) {
                    break;
                }

                if (d + 1 >= deviations.size()) {
                    double startValue = defaultValue;
                    if (d > 0 && this.deviations.get(d - 1).overlap(deviation) && !deviation.contains(this.deviations.get(d - 1))) {
                        startValue = this.deviations.get(d - 1).getValue();
                    }
                    this.deviations.add(new FullValueDeviation(deviation, deviation.getEnd() - deviation.getStart(), startValue, defaultValue));

                    d++;
                    break;
                }

                ValueDeviation nextDeviation = deviations.get(d + 1);

                if (deviation.contains(nextDeviation)) {
                    if (deviations.stream().filter(nextDeviation::overlap).count() != 1) {
                        throw new RuntimeException("There can't be multiple deviation overlaps");
                    }

                    if (deviation.getStart() + deviation.getStartRampLength() > nextDeviation.getStart()) {
                        throw new RuntimeException("Inner deviation can't overlap with outer start ramp");
                    }

                    if (deviation.getEnd() - deviation.getEndRampLength() < nextDeviation.getEnd()) {
                        throw new RuntimeException("Inner deviation can't overlap with outer end ramp");
                    }

                    this.deviations.add(new FullValueDeviation(nextDeviation, nextDeviation.getEnd() - nextDeviation.getStart(), deviation.getValue(), deviation.getValue()));

                    d++;
                } else if (deviation.overlap(nextDeviation)) {
                    double startValue = defaultValue;
                    if (d > 0 && this.deviations.get(d - 1).overlap(deviation)) {
                        startValue = this.deviations.get(d - 1).getValue();
                    }
                    this.deviations.add(new FullValueDeviation(deviation, nextDeviation.getStart() - deviation.getStart(), startValue, deviation.getValue()));

                    d++;
                    break;
                } else {
                    double startValue = defaultValue;
                    if (d > 0 && this.deviations.get(d - 1).overlap(deviation) && !deviation.contains(this.deviations.get(d - 1))) {
                        startValue = this.deviations.get(d - 1).getValue();
                    }
                    this.deviations.add(new FullValueDeviation(deviation, deviation.getEnd() - deviation.getStart(), startValue, defaultValue));

                    d++;
                    break;
                }
            }
        }

        this.deviations.sort(Comparator.comparingDouble(ValueDeviation::getStart));
        Collections.reverse(this.deviations);
    }

    public ValueInterpolator(double defaultValue, ValueDeviation... deviations) {
        this(defaultValue, Arrays.asList(deviations));
    }

    private static double interpolate(double startValue, double endValue, double length, double position) {
        return ((endValue - startValue) / length) * position + startValue;
    }

    public double getValue(double distance) {
        Optional<FullValueDeviation> deviationOptional = deviations.stream().filter(deviation -> deviation.contains(distance)).findFirst();

        if (!deviationOptional.isPresent()) {
            return defaultValue;
        }

        return deviationOptional.get().getValueForDistance(distance);
    }

    public static class ValueDeviation {

        private final double value;
        private final double start;
        private final double end;
        private final double startRampLength;
        private final double endRampLength;

        public ValueDeviation(double value, double start, double end, double startRampLength, double endRampLength) {
            this.value = value;
            this.start = start;
            this.end = end;
            this.startRampLength = startRampLength;
            this.endRampLength = endRampLength;

            if (this.end <= this.start) {
                throw new RuntimeException("The start of a ValueDeviation must be before the end");
            }

            if (this.startRampLength + this.endRampLength > this.end - this.start) {
                throw new RuntimeException("Ramp lengths must sum to less than the total ValueDeviation length");
            }
        }

        public ValueDeviation(double value, double start, double end, double rampLength) {
            this(value, start, end, rampLength, rampLength);
        }

        public ValueDeviation(double value, double start, double end) {
            this(value, start, end, 0.0);
        }

        public double getValue() {
            return value;
        }

        public double getStart() {
            return start;
        }

        public double getEnd() {
            return end;
        }

        public double getStartRampLength() {
            return startRampLength;
        }

        public double getEndRampLength() {
            return endRampLength;
        }

        public boolean contains(double position) {
            return getStart() <= position && position <= getEnd();
        }

        public boolean contains(ValueDeviation deviation) {
            return getStart() < deviation.getStart() && deviation.getEnd() < getEnd();
        }

        public boolean overlap(ValueDeviation deviation) {
            return (getStart() < deviation.getStart() && deviation.getStart() < getEnd()) || (getStart() < deviation.getEnd() && deviation.getEnd() < getEnd()) || contains(deviation) || deviation.contains(this);
        }

        public String toString() {
            return "ValueDeviation of " + getValue() + " from " + getStart() + " to " + getEnd();
        }
    }

    private static class FullValueDeviation extends ValueDeviation {

        private final double actualLength;
        private final double startValue;
        private final double endValue;

        public FullValueDeviation(ValueDeviation deviation, double actualLength, double startValue, double endValue) {
            super(deviation.getValue(), deviation.getStart(), deviation.getEnd(), deviation.getStartRampLength(), deviation.getEndRampLength());

            this.actualLength = actualLength;
            this.startValue = startValue;
            this.endValue = endValue;

            if(getStartRampLength() + getEndRampLength() > actualLength) {
                throw new RuntimeException("Ramp lengths must sum to less than the total FullValueDeviation length");
            }
        }

        public double getActualLength() {
            return actualLength;
        }

        public double getStartValue() {
            return startValue;
        }

        public double getEndValue() {
            return endValue;
        }

        public double getValueForDistance(double distance) {
            if (distance <= getStart()) {
                return getStartValue();
            }

            if (distance >= getEnd()) {
                return getEndValue();
            }

            if (distance - getStart() < getStartRampLength()) {
                return interpolate(getStartValue(), getValue(), getStartRampLength(), distance - getStart());
            }

            if (getEnd() - distance < getEndRampLength()) {
                return interpolate(getValue(), getEndValue(), getEndRampLength(), getEndRampLength() - (getEnd() - distance));
            }

            return getValue();
        }
    }
}