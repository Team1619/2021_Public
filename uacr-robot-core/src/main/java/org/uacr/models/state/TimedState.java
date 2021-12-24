package org.uacr.models.state;

import org.uacr.robot.AbstractModelFactory;
import org.uacr.utilities.Config;
import org.uacr.utilities.Sets;
import org.uacr.utilities.Timer;
import org.uacr.utilities.YamlConfigParser;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

import java.util.Set;

/**
 * A shell that runs a state until it is done or it times out
 * Runs a single state until it is done or times out
 */

public class TimedState implements State {

    private static final Logger logger = LogManager.getLogger(TimedState.class);

    private final Timer timer;
    private final State subState;
    private final int timeout;
    private final String stateName;
    private final String subStateName;

    /**
     * @param modelFactory so it can create its substate
     * @param name of the TimedState
     * @param parser yaml parser for the TimedState
     * @param config for the TimedState
     */

    public TimedState(AbstractModelFactory modelFactory, String name, YamlConfigParser parser, Config config) {
        timer = new Timer();
        stateName = name;
        timeout = config.getInt("timeout");
        subStateName = config.getString("state");

        subState = modelFactory.createState(subStateName, parser, parser.getConfig(stateName));
    }

    /**
     * @return the substate
     */

    @Override
    public Set<State> getSubStates() {
        return Sets.of(subState);
    }

    /**
     * Starts the timout timer
     */

    @Override
    public void initialize() {
        logger.debug("Entering Timed State {}", stateName);
        timer.start(timeout);
    }

    /**
     * Called every frame
     */

    @Override
    public void update() {
    }

    /**
     * Resets the timout timer
     */

    @Override
    public void dispose() {
        logger.trace("Leaving Timed State {}", stateName);
        timer.reset();
    }

    /**
     * @return true if the substate is done or the timout timer is done
     */

    @Override
    public boolean isDone() {
        return subState.isDone() || timer.isDone();
    }

    /**
     * @return the subsystems used by the substate
     */

    @Override
    public Set<String> getSubsystems() {
        return subState.getSubsystems();
    }

    /**
     * @return the name of the Timed State
     */

    @Override
    public String getName() {
        return stateName;
    }

    /**
     * This method is used so the .toString will return the name of the state if called on this object
     * @return the name of the Timed State
     */
    @Override
    public String toString() {
        return getName();
    }
}
