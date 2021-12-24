package org.team1619;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;

import org.team1619.modelfactory.RobotModelFactory;
import org.team1619.robot.FrcHardwareRobot;
import org.team1619.state.StateControls;
import org.uacr.robot.AbstractModelFactory;
import org.uacr.robot.AbstractStateControls;
import org.uacr.robot.RobotCore;
import org.uacr.shared.abstractions.FMS;

public class Robot extends TimedRobot {

	private final RobotCore robot;

	public Robot() {
		robot = new FrcHardwareRobot() {
			@Override
			protected AbstractStateControls createStateControls() {
				return new StateControls(inputValues, robotConfiguration);
			}

			@Override
			protected AbstractModelFactory createModelFactory() {
				return new RobotModelFactory(hardwareFactory, inputValues, outputValues, robotConfiguration, objectsDirectory);
			}
		};
	}

	public static void main(String... args) {
		RobotBase.startRobot(Robot::new);
	}

	@Override
	public void robotInit() {
		robot.start();
	}

	@Override
	public void teleopInit() {
		robot.getFms().setMode(FMS.Mode.TELEOP);
	}

	@Override
	public void autonomousInit() {
		robot.getFms().setMode(FMS.Mode.AUTONOMOUS);
	}

	@Override
	public void disabledInit() {
		robot.getFms().setMode(FMS.Mode.DISABLED);
	}

	@Override
	public void testInit() {
		robot.getFms().setMode(FMS.Mode.TEST);
	}
}
