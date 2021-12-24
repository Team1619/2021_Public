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

    private boolean isPriming;

    public TeleopModeLogic(InputValues inputValues, RobotConfiguration robotConfiguration) {
        super(inputValues, robotConfiguration);

        isPriming = false;
    }

    @Override
    public void initialize() {
        logger.info("***** TELEOP *****");

        isPriming = false;
    }

    @Override
    public void update() {
        //TODO Buttons should be read in from robot configuration.
        if (sharedInputValues.getBooleanRisingEdge("ipb_operator_right_bumper")) {
            isPriming = !isPriming;
        } else if(sharedInputValues.getBooleanRisingEdge("ipb_operator_left_trigger") ||
                sharedInputValues.getBooleanRisingEdge("ipb_operator_left_bumper")){
            isPriming = false;
        }
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
            case "st_drivetrain_velocity":
                return sharedInputValues.getBoolean(robotConfiguration.getString("global_drivetrain", "velocity_mode_button"));

            //Collector
            case "st_collector_zero":
                return !sharedInputValues.getBoolean("ipb_collector_has_been_zeroed");
            // Because isReady is only called on inactive states we can toggle between two states on one button
            case "st_collector_retract":
                return sharedInputValues.getBooleanRisingEdge("ipb_operator_left_bumper");

            // Hopper
            case "st_hopper_zero":
                return !sharedInputValues.getBoolean("ipb_hopper_has_been_zeroed");

            // Elevator
            case "st_elevator_zero":
                return !sharedInputValues.getBoolean("ipb_elevator_has_been_zeroed");

            // Turret
            case "st_turret_zero":
                return !sharedInputValues.getBoolean("ipb_turret_has_been_zeroed");

            //Flywheel
            case "st_flywheel_zero":
                return !sharedInputValues.getBoolean("ipb_flywheel_has_been_zeroed");

            // Climber
            case "st_climber_zero":
                return !sharedInputValues.getBoolean("ipb_climber_has_been_zeroed");

            // Sequences and Parallels
            case "pl_prime_to_shoot":
                return isPriming;
            case "pl_shoot":
                return sharedInputValues.getBoolean("ipb_driver_right_trigger") && (sharedInputValues.getBoolean("ipb_primed_to_shoot") || sharedInputValues.getBoolean("ipb_operator_dpad_left")) && isPriming;
            case "pl_collect_floor":
                return sharedInputValues.getBoolean("ipb_operator_left_trigger");
            case "pl_dejam":
                return sharedInputValues.getBooleanRisingEdge("ipb_operator_dpad_up");

            // ------- Undefined states -------
            default:
                return false;
        }
    }

    @Override
    public boolean isDone(String name, State state) {
        switch (name) {
            // Drivetrain
            case "st_drivetrain_velocity":
                return !sharedInputValues.getBoolean(robotConfiguration.getString("global_drivetrain", "velocity_mode_button"));

 			// Collector
			case "st_collector_retract":
				return sharedInputValues.getBooleanRisingEdge("ipb_operator_left_bumper");

            // Turret

			// Sequences and Parallels
			case "pl_prime_to_shoot":
				return !isPriming;
			case "pl_shoot":
				return sharedInputValues.getBooleanFallingEdge("ipb_driver_right_trigger");
			case "pl_collect_floor":
				return !sharedInputValues.getBoolean("ipb_operator_left_trigger");
			case "pl_dejam":
				return sharedInputValues.getBooleanFallingEdge("ipb_operator_dpad_up");

            // ------- Undefined states -------
            default:
                return state.isDone();
        }
    }
}
