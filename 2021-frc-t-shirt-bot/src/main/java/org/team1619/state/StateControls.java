package org.team1619.state;

import org.team1619.state.modelogic.*;
import org.uacr.robot.AbstractStateControls;
import org.uacr.robot.ControlMode;
import org.uacr.shared.abstractions.FMS;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.shared.abstractions.RobotConfiguration;
import org.uacr.utilities.Timer;
import org.uacr.utilities.injection.Inject;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

/**
 * Selects the robot status and control modes to be used by the RobotManager for competition bot
 */

public class StateControls extends AbstractStateControls {

	private static final Logger logger = LogManager.getLogger(StateControls.class);

	private final Timer fTimerMode;
	private final Timer fTimerEndgame;
	private final boolean fInitialIsManualMode;

	private boolean isEndgameMode;
	private boolean isManualMode;

	@Inject
	public StateControls(InputValues inputValues, RobotConfiguration robotConfiguration) {
		super(inputValues, robotConfiguration);

		registerRobotStatus(new RobotStatus(inputValues, robotConfiguration));
		registerModeLogic(ControlMode.AUTONOMOUS, new AutonomousModeLogic(inputValues, robotConfiguration));
		registerModeLogic(ControlMode.TELEOP, new TeleopModeLogic(inputValues, robotConfiguration));
		registerModeLogic(ControlMode.MANUAL_TELEOP, new ManualTeleopModeLogic(inputValues, robotConfiguration));
		registerModeLogic(ControlMode.ENDGAME, new EndgameModeLogic(inputValues, robotConfiguration));
		registerModeLogic(ControlMode.MANUAL_ENDGAME, new ManualEndgameModeLogic(inputValues, robotConfiguration));

		//Modes
		isEndgameMode = false;
		isManualMode = false;

		fTimerMode = new Timer();
		fmsMode = FMS.Mode.DISABLED;
		//Climb
		fTimerEndgame = new Timer();
		if (robotConfiguration.contains("general", "initial_teleop_mode")) {
			switch (robotConfiguration.getString("general", "initial_teleop_mode")) {
				case "teleop_mode":
					fInitialIsManualMode = false;
					break;
				case "manual_mode":
					fInitialIsManualMode = true;
					break;
				default:
					fInitialIsManualMode = false;
					logger.error("Mode specified does not exist");
			}
		} else {
			fInitialIsManualMode = false;
		}
	}

	@Override
	public void initialize(FMS.Mode currentFmsMode) {
		fmsMode = currentFmsMode;

		isEndgameMode = false;
		isManualMode = fInitialIsManualMode;

		sharedInputValues.setBoolean("ipb_endgame_enabled", false);
	}

	@Override
	public void update() {

		if (fmsMode == FMS.Mode.AUTONOMOUS) {
			setCurrentControlMode(ControlMode.AUTONOMOUS);
		} else {
			if (!fTimerMode.isStarted() && sharedInputValues.getBooleanRisingEdge("ipb_operator_start")) {
				fTimerMode.start(1000);
			} else if (fTimerMode.isDone()) {
				isManualMode = !isManualMode;
				fTimerMode.reset();
			} else if (!sharedInputValues.getBoolean("ipb_operator_start")) {
				fTimerMode.reset();
			}

			if (!fTimerEndgame.isStarted() && sharedInputValues.getBooleanRisingEdge("ipb_operator_back")) {
				fTimerEndgame.start(1000);
			} else if (fTimerEndgame.isDone()) {
				isEndgameMode = !isEndgameMode;
				fTimerEndgame.reset();
			} else if (!sharedInputValues.getBoolean("ipb_operator_back")) {
				fTimerEndgame.reset();
			}

			if (isManualMode) {
				if (isEndgameMode) {
					setCurrentControlMode(ControlMode.MANUAL_ENDGAME);
				} else {
					setCurrentControlMode(ControlMode.MANUAL_TELEOP);
				}
			} else {
				if (isEndgameMode) {
					setCurrentControlMode(ControlMode.ENDGAME);
				} else {
					setCurrentControlMode(ControlMode.TELEOP);
				}
			}

			sharedInputValues.setBoolean("ipb_endgame_enabled", isEndgameMode);
		}
		sharedInputValues.setString("ips_mode", getCurrentControlMode().toString());
	}

	@Override
	public void dispose() {
		sharedInputValues.setString("ips_mode", "DISABLED");
	}
}
