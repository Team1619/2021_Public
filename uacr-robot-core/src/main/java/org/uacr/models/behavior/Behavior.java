package org.uacr.models.behavior;

import org.uacr.utilities.Config;

import java.util.Set;

/**
 * The base for all robot behavior.
 * A behavior is logic used by a state to preform a single action on the robot.
 */

public interface Behavior {

    /**
     * @return a list of subsystems required by the behavior
     */
    Set<String> getSubsystems();

    /**
     * Initializes the behavior to run the requested state
     * This is a place to read values from the state config file
     * This is called by the StateMachine on the first frame a state becomes active
     * @param stateName The name of the state being initialized
     * @param config    Contains the information under the name of the state in the state.yaml file
     */
    void initialize(String stateName, Config config);

    /**
     * Called every frame when a state is active
     * This is a place to read buttons, do calculations, set motor outputs...
     */
    void update();

    /**
     * This holds the logic that determines when a state has finished its task
     * In state mode this logic is largely ignored unless the state requests disposal (see idRequestingDisposal) when it is finished
     * In Sequence mode this logic lets the sequencer that is running this state know when to move onto the next state
     * This logic can be overridden in implementations of StateControls for specific states
     * @return true or false
     */
    boolean isDone();

    /**
     * Called when the state becomes inactive
     * This is a place to set motors to 0.0, clear variables, post positions... Anything you want to happen when the state finishes.
     */
    void dispose();
}
