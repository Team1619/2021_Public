package org.uacr.robot;

import org.uacr.models.state.State;
import org.uacr.shared.abstractions.FMS;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.shared.abstractions.RobotConfiguration;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

import javax.annotation.Nullable;

/**
 * Manages current control mode, isReady and isDone logic, and the status of the robot
 */

public class RobotManager {

    private static final Logger logger = LogManager.getLogger(RobotManager.class);

    protected final InputValues sharedInputValues;
    protected final RobotConfiguration robotConfiguration;
    private AbstractStateControls stateControls;
    @Nullable
    private AbstractModeLogic lastModeLogic;

    public RobotManager(InputValues inputValues, RobotConfiguration robotConfiguration, AbstractStateControls stateControls) {
        sharedInputValues = inputValues;
        this.robotConfiguration = robotConfiguration;
        this.stateControls = stateControls;
        lastModeLogic = null;
    }

    /**
     * Called when switching into Teleop or Auto
     */
    public void initialize(FMS.Mode currentFmsMode) {
        // Initializes the robot status
        stateControls.getRobotStatus().initialize();

        // Initializes and runs a frame of state controls to select the correct robot control mode
        stateControls.initialize(currentFmsMode);
        stateControls.update();
    }


    public void disabledUpdate() {
        stateControls.getRobotStatus().disabledUpdate();
    }

    /**
     * Called every frame to update robot status and current mode logic
     */
    public void update() {
        // Updates the robot status
        stateControls.getRobotStatus().update();

        // Updates state controls to select the correct mode logic
        stateControls.update();

        // Gets the current mode logic from state controls
        AbstractModeLogic currentModeLogic = stateControls.getCurrentModeLogic();

        // If the requested mode logic has changed dispose the old mode and initialize the new mode logic
        if (currentModeLogic != lastModeLogic) {
            // Dispose the old mode logic if it exists
            if (lastModeLogic != null) {
                lastModeLogic.dispose();
            }

            // Initialize the new mode logic
            currentModeLogic.initialize();
        }

        // Updated the current mode logic
        currentModeLogic.update();

        // Set the last mode logic to the current mode logic
        lastModeLogic = currentModeLogic;
    }

    /**
     * Called when switching between Auto, Teleop and Disabled
     */
    public void dispose() {
        logger.info("Leaving {} mode", stateControls.getCurrentControlMode().toString());

        // Disposes the robot status
        stateControls.getRobotStatus().dispose();

        // Disposes state controls
        stateControls.dispose();

        // Disposes the current mode logic
        if (lastModeLogic != null) {
            lastModeLogic.dispose();
        }

        // Set the last mode logic to null so when the next mode logic is selected it will be initialized
        lastModeLogic = null;
    }

    /**
     * Defines when each state is ready
     *
     * @param name the name of the state being checked if it's ready
     * @return whether the state is ready
     */
    public final boolean isReady(String name) {
        // Calls isReady on the current mode logic based on the mode selected by state controls
        return stateControls.getCurrentModeLogic().isReady(name);
    }

    /**
     * Determines when a state is done
     * This is a place to override the isDone logic in the behavior for a specific state
     *
     * @param name  the state being checked if it's done
     * @param state the state object (used to call the behavior's isDone)
     * @return Whether the state is done
     */
    public final boolean isDone(String name, State state) {
        // Calls isDone on the current mode logic based on the mode selected by state controls
        return stateControls.getCurrentModeLogic().isDone(name, state);
    }
}
