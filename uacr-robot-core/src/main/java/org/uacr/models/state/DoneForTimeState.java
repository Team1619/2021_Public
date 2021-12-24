package org.uacr.models.state;

import org.uacr.robot.AbstractModelFactory;
import org.uacr.utilities.Config;
import org.uacr.utilities.Timer;
import org.uacr.utilities.YamlConfigParser;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * A shell that runs a state until it is done plus a certain amount of time has passed or it times out
 */

public class DoneForTimeState implements State {

    /**
     * Shell class for all DoneForTimeStates
     * DoneForTimeStates wrap a single state, parallel or sequence and pass it to the state machine to be run using two parameters
     * a state timeout ends the state a specified amount of time after isDone goes true
     * and a max timeout cause the state to end if isDone does not go true by that time.
     */

    private static final Logger logger = LogManager.getLogger(DoneForTimeState.class);

    private final Timer stateTimer;
    private final Timer maxTimer;
    private final State subState;
    private final int stateTimeout;
    private final int maxTimeout;
    private final String stateName;
    private final String subStateName;

    /**
     * @param modelFactory so the doneForTimeState can create its substate
     * @param name the name of the DoneForTimeState
     * @param parser the yaml parser for the DoneForTimeState
     * @param config the config of the DoneForTimeState
     */

    public DoneForTimeState(AbstractModelFactory modelFactory, String name, YamlConfigParser parser, Config config) {
        stateTimer = new Timer();
        maxTimer = new Timer();
        stateTimeout = config.getInt("state_timeout");
        maxTimeout = config.getInt("max_timeout", -1);
        stateName = name;
        subStateName = config.getString("state");

        subState = modelFactory.createState(subStateName, parser, parser.getConfig(subStateName));
    }

    /**
     * @return a list of the child state and all the states contained within the substate
     */

    @Override
    public Set<State> getSubStates() {
        // Returns a list of all the states it is currently running
        Set<State> states = new HashSet<>();
        states.add(subState);
        states.addAll(subState.getSubStates());
        return states;
    }

    /**
     * To initialize check if a max timout has been specified if so start the max timeout timer
     */

    @Override
    public void initialize() {
        logger.debug("Entering Done For Time State {}", stateName);
        if (maxTimeout != -1) {
            maxTimer.start(maxTimeout);
        }
    }

    /**
     * Called every frame
     */

    @Override
    public void update() {

    }

    /**
     * Reset the state timer to clear it for the next time the state is used
     */

    @Override
    public void dispose() {
        logger.trace("Leaving Done For Time State {}", stateName);
        stateTimer.reset();
    }

    /**
     * Start a timer with the specified state timeout time when the state isDone
     * @return true if the state timeout timer or the max timout timer finishes
     */

    @Override
    public boolean isDone() {
        //Start a timer when the state is done
        if (subState.isDone()) {
            if (!stateTimer.isStarted()) {
                stateTimer.start(stateTimeout);
            }

        } else if (stateTimer.isStarted()) {
            stateTimer.reset();
        }

        return stateTimer.isDone() || maxTimer.isDone();
    }

    /**
     * @return a list of the subsystems required by the substate
     */

    @Override
    public Set<String> getSubsystems() {
        return subState.getSubsystems();
    }

    /**
     * @return the name of the DoneForTimeState
     */

    @Override
    public String getName() {
        return stateName;
    }

    /**
     * This method is used so the .toString will return the name of the state if called on this object
     * @return the name of the DoneForTimeState
     */

    @Override
    public String toString() {
        return getName();
    }
}
