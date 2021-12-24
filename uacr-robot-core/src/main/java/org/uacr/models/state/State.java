package org.uacr.models.state;

import java.util.Set;

/**
 * The base class for all states.
 * States are implementations of behaviors and use a behavior with the specific configuration parameters.
 */

public interface State {

    /**
     * @return the name of the state
     */
    String getName();

    /**
     * @return a list of subsystems required by the state
     */
    Set<String> getSubsystems();

    /**
     * @return a list of states managed by this state
     * A SingleState returns itself
     * SequencerState, ParallelState, DoneForTimeState, and TimedState return the list of states they are managing in the current frame
     */
    Set<State> getSubStates();

    /**
     * Called when a state becomes active
     */
    void initialize();

    /**
     * Called every frame when a state is active
     */
    void update();

    /**
     * @return whether the state has completed its task
     * This can be determined by the behavior's isDone, when a timer runs out, the sequence is finished ...
     */
    boolean isDone();

    /**
     * Called when the state becomes inactive
     */
    void dispose();
}
