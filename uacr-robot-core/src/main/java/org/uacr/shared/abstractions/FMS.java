package org.uacr.shared.abstractions;

/**
 * Stores the current FMS Mode
 */

public interface FMS {

    Mode getMode();

    void setMode(Mode mode);

    enum Mode {
        AUTONOMOUS,
        TELEOP,
        DISABLED,
        TEST
    }
}
