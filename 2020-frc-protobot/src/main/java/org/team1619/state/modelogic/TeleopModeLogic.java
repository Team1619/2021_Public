package org.team1619.state.modelogic;

import org.uacr.models.state.State;
import org.uacr.robot.AbstractModeLogic;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.shared.abstractions.RobotConfiguration;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

/**
 * Handles the isReady and isDone logic for teleop mode on competition bot
 */

public class TeleopModeLogic extends AbstractModeLogic {

    private static final Logger logger = LogManager.getLogger(TeleopModeLogic.class);

    public TeleopModeLogic(InputValues inputValues, RobotConfiguration robotConfiguration) {
        super(inputValues, robotConfiguration);

    }

    @Override
    public void initialize() {
        logger.info("***** TELEOP *****");

    }

    @Override
    public void update() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean isReady(String name) {
        switch (name) {
            //Drivetrain
            case "st_drivetrain_zero":
                return !sharedInputValues.getBoolean("ipb_drivetrain_has_been_zeroed");

            // ------- Undefined states -------
            default:
                return false;
        }
    }

    @Override
    public boolean isDone(String name, State state) {
        switch (name) {
            // Drivetrain

            // ------- Undefined states -------
            default:
                return state.isDone();
        }
    }
}
