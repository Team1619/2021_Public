package org.team1619.state;

import org.team1619.modelfactory.SimModelFactory;
import org.team1619.shared.abstractions.Dashboard;
import org.team1619.shared.concretions.sim.SimDashboard;
import org.uacr.robot.AbstractModelFactory;
import org.uacr.robot.AbstractModule;
import org.uacr.robot.AbstractStateControls;

public class SimModule extends AbstractModule {
    @Override
    public void configureModeSpecificConcretions() {
        bind(Dashboard.class, SimDashboard.class);

        bind(AbstractModelFactory.class, SimModelFactory.class);
        bind(AbstractStateControls.class, StateControls.class);
    }
}
