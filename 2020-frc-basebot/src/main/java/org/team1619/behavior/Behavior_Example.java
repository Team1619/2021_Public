package org.team1619.behavior;

import org.uacr.models.behavior.Behavior;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.shared.abstractions.OutputValues;
import org.uacr.shared.abstractions.RobotConfiguration;
import org.uacr.utilities.Config;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;
import org.uacr.utilities.Timer;

import java.util.Set;

/**
 * Example behavior to copy for other behaviors
 */

public class Behavior_Example implements Behavior {

	private static final Logger logger = LogManager.getLogger(Behavior_Example.class);
	private static final Set<String> subsystems = Set.of("ss_example");

	private final InputValues sharedInputValues;
	private final OutputValues sharedOutputValues;
	private final String whatThisButtonDoes;
	private Timer timer;

	private int configurationValue;

	public Behavior_Example(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
		sharedInputValues = inputValues;
		sharedOutputValues = outputValues;
		whatThisButtonDoes = robotConfiguration.getString("global_example", "what_this_button_does");

		configurationValue = 0;
		timer = new Timer();
	}

	@Override
	public void initialize(String stateName, Config config) {
		logger.debug("Entering state {}", stateName);

		configurationValue = config.getInt("config_key", 0);
		timer.start(configurationValue);
	}

	@Override
	public void update() {
		boolean whatThisButtonDoes = sharedInputValues.getBoolean(this.whatThisButtonDoes);
		sharedInputValues.setBoolean("opb_example", whatThisButtonDoes);
	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean isDone() {
		return timer.isDone();
	}

	@Override
	public Set<String> getSubsystems() {
		return subsystems;
	}
}