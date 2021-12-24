package org.team1619.robot;

import org.team1619.services.logging.LoggingService;
import org.team1619.shared.concretions.sim.SimDashboard;
import org.uacr.robot.RobotCore;
import org.uacr.services.webdashboard.WebDashboardService;
import org.uacr.utilities.RobotSystem;
import org.uacr.utilities.services.Service;

import java.util.List;

import static org.uacr.utilities.RobotSystem.RuntimeMode.SIM_MODE;

public abstract class FrcSimRobot extends RobotCore {

    public FrcSimRobot(){
        RobotSystem.setRuntimeMode(SIM_MODE);
    }

    @Override
    protected List<Service> createInfoServices() {
        return List.of(
                new LoggingService(inputValues, outputValues, robotConfiguration, new SimDashboard()),
                new WebDashboardService(eventBus, fms, inputValues, outputValues, robotConfiguration));
    }
}
