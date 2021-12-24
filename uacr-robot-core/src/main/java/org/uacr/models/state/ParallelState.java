package org.uacr.models.state;

import org.uacr.robot.AbstractModelFactory;
import org.uacr.utilities.Config;
import org.uacr.utilities.YamlConfigParser;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * A shell that handles running multiple states simultaneously
 * Assembles a list of all the states it wants to run and passes it to the state machine
 * Can take two types of states
 * Foreground states are states that the parallel has to finish before it is done
 * and Background states run the entire time the parallel is running but have no impact on when it is done
 */

public class ParallelState implements State {

    private static final Logger logger = LogManager.getLogger(ParallelState.class);

    private final AbstractModelFactory modelFactory;
    private final Set<State> foregroundStates;
    private final Set<State> backgroundStates;
    private final String stateName;

    /**
     * @param modelFactory so the parallel state can creat its substates
     * @param name of the parallel state
     * @param parser yaml parser for the parallel state
     * @param config for the parallel state
     */

    public ParallelState(AbstractModelFactory modelFactory, String name, YamlConfigParser parser, Config config) {
        this.modelFactory = modelFactory;
        foregroundStates = new HashSet<>();
        backgroundStates = new HashSet<>();
        stateName = name;

        // Reads in the states that will be run until they are all finished and creates a list
        for (Object foregroundStateName : config.getList("foreground_states")) {
            foregroundStates.add(modelFactory.createState((String) foregroundStateName, parser, parser.getConfig(foregroundStateName)));
        }

        // Reads in the states that will be run the entire time the parallel is running and creates a list
        if (config.contains("background_states")) {
            for (Object backgroundStateName : config.getList("background_states")) {
                backgroundStates.add(modelFactory.createState((String) backgroundStateName, parser, parser.getConfig(backgroundStateName)));
            }
        }
    }

    /**
     * @return a list of all the states being used by the parallel
     */

    @Override
    public Set<State> getSubStates() {
        Set<State> states = new HashSet<>();
        states.addAll(foregroundStates);
        states.addAll(backgroundStates);
        for (State foregroundState : foregroundStates) {
            states.addAll(foregroundState.getSubStates());
        }
        for (State backgroundState : backgroundStates) {
            states.addAll(backgroundState.getSubStates());
        }
        return states;
    }

    /**
     * Called  at the beginning of the state
     */

    @Override
    public void initialize() {
        logger.debug("Entering Parallel State {}", stateName);
    }

    /**
     * Called every frame
     */

    @Override
    public void update() {

    }

    /**
     * Called when the state finishes
     */

    @Override
    public void dispose() {
        logger.trace("Leaving Parallel State {}", stateName);
    }

    /**
     * @return true if all the foreground states are done
     */

    @Override
    public boolean isDone() {
        for (State foregroundState : foregroundStates) {
            if (!foregroundState.isDone()) {
                return false;
            }
        }

        return true;
    }

    /**
     * @return a list of all the subsystems used
     */

    @Override
    public Set<String> getSubsystems() {
        // Returns a list of all the subsystems required by all the states it is running
        Set<String> subsystems = new HashSet<>();
        for (State state : foregroundStates) {
            subsystems.addAll(state.getSubsystems());
        }
        for (State state : backgroundStates) {
            subsystems.addAll(state.getSubsystems());
        }
        return subsystems;
    }

    /**
     * @return the name of the parallel
     */

    @Override
    public String getName() {
        return stateName;
    }

    /**
     * This method is used so the .toString will return the name of the state if called on this object
     * @return the name of the parallel
     */

    @Override
    public String toString() {
        return getName();
    }
}
