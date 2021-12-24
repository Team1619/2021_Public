package org.team1619.state;

import org.team1619.modelfactory.RobotModelFactory;
import org.team1619.shared.abstractions.Dashboard;
import org.team1619.shared.concretions.robot.RobotDashboard;
import org.uacr.robot.AbstractModelFactory;
import org.uacr.robot.AbstractModule;
import org.uacr.robot.AbstractStateControls;

public class RobotModule extends AbstractModule {
	@Override
	public void configureModeSpecificConcretions() {
		bind(Dashboard.class, RobotDashboard.class);

		bind(AbstractModelFactory.class, RobotModelFactory.class);
		bind(AbstractStateControls.class, StateControls.class);
	}
}
