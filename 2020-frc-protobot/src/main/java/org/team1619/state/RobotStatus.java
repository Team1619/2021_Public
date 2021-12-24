package org.team1619.state;

import org.uacr.robot.AbstractRobotStatus;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.shared.abstractions.RobotConfiguration;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

/**
 * Sets flags and does global math and logic for competition bot
 */

public class RobotStatus extends AbstractRobotStatus {

    private static final Logger logger = LogManager.getLogger(RobotStatus.class);

    public RobotStatus(InputValues inputValues, RobotConfiguration robotConfiguration) {
        super(inputValues, robotConfiguration);

    }

    @Override
    public void initialize() {
        // Zero
        if (!sharedInputValues.getBoolean("ipb_robot_has_been_zeroed")) {
            sharedInputValues.setBoolean("ipb_drivetrain_has_been_zeroed", false);
            sharedInputValues.setInputFlag("ipv_navx", "zero");
        }

    }

    @Override
    public void update() {

        if (!sharedInputValues.getBoolean("ipb_robot_has_been_zeroed") &&
                sharedInputValues.getBoolean("ipb_drivetrain_has_been_zeroed")) {

            sharedInputValues.setBoolean("ipb_robot_has_been_zeroed", true);
        }
    }

    @Override
    public void dispose() {

    }
}
