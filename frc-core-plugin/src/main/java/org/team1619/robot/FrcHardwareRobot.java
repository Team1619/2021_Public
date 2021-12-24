package org.team1619.robot;

import org.team1619.services.logging.LoggingService;
import org.team1619.shared.concretions.robot.RobotDashboard;
import org.uacr.robot.RobotCore;
import org.uacr.services.webdashboard.WebDashboardService;
import org.uacr.utilities.RobotSystem;
import org.uacr.utilities.services.Service;

import java.util.List;

import static org.uacr.utilities.RobotSystem.RuntimeMode.HARDWARE_MODE;

public abstract class FrcHardwareRobot extends RobotCore {

    public FrcHardwareRobot(){
        RobotSystem.setRuntimeMode(HARDWARE_MODE);
    }

    @Override
    protected List<Service> createInfoServices() {
        return List.of(
                new LoggingService(inputValues, outputValues, robotConfiguration, new RobotDashboard(inputValues)),
                new WebDashboardService(eventBus, fms, inputValues, outputValues, robotConfiguration));
    }
}
