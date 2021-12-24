package org.uacr.robot;

import org.uacr.shared.abstractions.InputValues;
import org.uacr.shared.abstractions.RobotConfiguration;

/**
 * The base class for updating the robot status
 * Concretions of this class should be used for setting flags and doing global math and logic
 */

public abstract class AbstractRobotStatus {

    protected final InputValues sharedInputValues;
    protected final RobotConfiguration robotConfiguration;

    public AbstractRobotStatus(InputValues inputValues, RobotConfiguration robotConfiguration) {
        sharedInputValues = inputValues;
        this.robotConfiguration = robotConfiguration;
    }

    /**
     * Called when switching into Teleop or Auto
     * This is a place to zero subsystems, clear variables...
     */
    public abstract void initialize();

    /**
     * Called every frame
     * This is a place to set flags and do global math and logic
     */
    public abstract void update();

    /**
     * Called every frame when FMS Mode is Disabled
     * This is a place for any code that has to run even when disabled (for example selecting path when disabled)
     */

    public void disabledUpdate() {
    }

    /**
     * Called when switching between Auto, Teleop and Disabled
     * This is a place for any clean up, clearing variables...
     */
    public abstract void dispose();
}
