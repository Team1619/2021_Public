package org.team1619.modelfactory;

import org.team1619.behavior.*;
import org.uacr.models.behavior.Behavior;
import org.uacr.models.exceptions.ConfigurationException;
import org.uacr.robot.AbstractModelFactory;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.shared.abstractions.ObjectsDirectory;
import org.uacr.shared.abstractions.OutputValues;
import org.uacr.shared.abstractions.RobotConfiguration;
import org.uacr.utilities.Config;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

public class ModelFactory_Behaviors extends AbstractModelFactory {

	private static final Logger logger = LogManager.getLogger(ModelFactory_Behaviors.class);

	private final InputValues sharedInputValues;
	private final OutputValues sharedOutputValues;
	private final RobotConfiguration robotConfiguration;

	public ModelFactory_Behaviors(InputValues inputValues, OutputValues outputValues, RobotConfiguration robotConfiguration, ObjectsDirectory objectsDirectory) {
		super(inputValues, outputValues, robotConfiguration, objectsDirectory);
		sharedInputValues = inputValues;
		sharedOutputValues = outputValues;
		this.robotConfiguration = robotConfiguration;
	}

	public Behavior createBehavior(String name, Config config) {
		logger.trace("Creating behavior '{}' of type '{}' with config '{}'", name, config.getType(), config.getData());

		switch (name) {
			// Drivetrain
			case "bh_drivetrain_zero":
				return new Drivetrain_Zero(sharedInputValues, sharedOutputValues, config, robotConfiguration);
			case "bh_drivetrain":
				return new Drivetrain(sharedInputValues, sharedOutputValues, config, robotConfiguration);
			case "bh_drivetrain_limelight_align":
				return new Drivetrain_Limelight_Align(sharedInputValues, sharedOutputValues, config, robotConfiguration);
			case "bh_drivetrain_pure_pursuit":
				return new Drivetrain_Pure_Pursuit(sharedInputValues, sharedOutputValues, config, robotConfiguration);

			// Collector
			case "bh_collector_zero":
				return new Collector_Zero(sharedInputValues, sharedOutputValues, config, robotConfiguration);
			case "bh_collector_manual":
				return new Collector_Manual(sharedInputValues, sharedOutputValues, config, robotConfiguration);
			case "bh_collector_states":
				return new Collector_States(sharedInputValues, sharedOutputValues, config, robotConfiguration);

			// Hopper
			case "bh_hopper_zero":
				return new Hopper_Zero(sharedInputValues, sharedOutputValues, config, robotConfiguration);
			case "bh_hopper_home":
				return new Hopper_Home(sharedInputValues, sharedOutputValues, config, robotConfiguration);
			case "bh_hopper_manual":
				return new Hopper_Manual(sharedInputValues, sharedOutputValues, config, robotConfiguration);
			case "bh_hopper_states":
				return new Hopper_States(sharedInputValues, sharedOutputValues, config, robotConfiguration);

			// Elevator
			case "bh_elevator_zero":
				return new Elevator_Zero(sharedInputValues, sharedOutputValues, config, robotConfiguration);
			case "bh_elevator_manual":
				return new Elevator_Manual(sharedInputValues, sharedOutputValues, config, robotConfiguration);
			case "bh_elevator_states":
				return new Elevator_States(sharedInputValues, sharedOutputValues, config, robotConfiguration);

			// Turret
			case "bh_turret_zero":
				return new Turret_Zero(sharedInputValues, sharedOutputValues, config, robotConfiguration);
			case "bh_turret_manual":
				return new Turret_Manual(sharedInputValues, sharedOutputValues, config, robotConfiguration);
			case "bh_turret_positions":
				return new Turret_Positions(sharedInputValues, sharedOutputValues, config, robotConfiguration);
			case "bh_turret_align":
				return new Turret_Align(sharedInputValues, sharedOutputValues, config, robotConfiguration);

			// Flywheel
			case "bh_flywheel_zero":
				return new Flywheel_Zero(sharedInputValues, sharedOutputValues, config, robotConfiguration);
			case "bh_flywheel_manual":
				return new Flywheel_Manual(sharedInputValues, sharedOutputValues, config, robotConfiguration);
			case "bh_flywheel_states":
				return new Flywheel_States(sharedInputValues, sharedOutputValues, config, robotConfiguration);

			// Climber
			case "bh_climber_zero":
				return new Climber_Zero(sharedInputValues,sharedOutputValues,config,robotConfiguration);
			case "bh_climber_states":
				return new Climber_States(sharedInputValues, sharedOutputValues, config, robotConfiguration);

			// State not found
			default:
				throw new ConfigurationException("Behavior " + name + " does not exist.");
		}
	}

}