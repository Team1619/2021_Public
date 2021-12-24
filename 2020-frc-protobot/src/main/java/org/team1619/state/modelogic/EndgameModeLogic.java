package org.team1619.state.modelogic;

import org.uacr.models.state.State;
import org.uacr.robot.AbstractModeLogic;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.shared.abstractions.RobotConfiguration;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

/**
 * Handles the isReady and isDone logic for endgame mode on competition bot
 */

public class EndgameModeLogic extends AbstractModeLogic {

    private static final Logger logger = LogManager.getLogger(EndgameModeLogic.class);

    public EndgameModeLogic(InputValues inputValues, RobotConfiguration robotConfiguration) {
        super(inputValues, robotConfiguration);
    }

    @Override
    public void initialize() {
        logger.info("***** ENDGAME *****");
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

            // ------- Undefined states -------
            default:
                return false;
        }
    }

    @Override
    public boolean isDone(String name, State state) {
        switch (name) {
            // ------- Undefined states -------
            default:
                return state.isDone();
        }
    }
}
