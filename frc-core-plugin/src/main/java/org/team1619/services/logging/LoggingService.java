package org.team1619.services.logging;

import org.team1619.shared.abstractions.Dashboard;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.shared.abstractions.OutputValues;
import org.uacr.shared.abstractions.RobotConfiguration;
import org.uacr.utilities.injection.Inject;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;
import org.uacr.utilities.services.ScheduledService;
import org.uacr.utilities.services.Scheduler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LoggingService implements ScheduledService {
    private static final Logger logger = LogManager.getLogger(LoggingService.class);

    private final InputValues sharedInputValues;
    private final OutputValues sharedOutputValues;
    private final RobotConfiguration robotConfiguration;
    private final Dashboard dashboard;

    private double previousTime;
    private long frameTimeThreshold;
    private long frameCycleTimeThreshold;

    private Set<String> desiredLogs;

    @Inject
    public LoggingService(InputValues inputValues, OutputValues outputValues, RobotConfiguration robotConfiguration, Dashboard dashboard) {
        sharedInputValues = inputValues;
        this.robotConfiguration = robotConfiguration;
        sharedOutputValues = outputValues;
        this.dashboard = dashboard;
        desiredLogs = new HashSet<>();
    }

    @Override
    public void startUp() throws Exception {
        logger.debug("Starting LoggingService");

        String valuesToLog = "Logging the following values: [";

        if (!robotConfiguration.categoryIsEmpty("log")) {
            Map<String, Object> logConfig = robotConfiguration.getCategory("log");
            for (String name : logConfig.keySet()) {
                if ((Boolean) logConfig.get(name)) {
                    desiredLogs.add(name);
                    valuesToLog += (name + ", ");
                }
            }
        }

        valuesToLog = valuesToLog.substring(0, valuesToLog.length() - 2) + "]";
        logger.trace(valuesToLog);

        previousTime = System.currentTimeMillis();
        frameTimeThreshold = robotConfiguration.getInt("global_timing", "frame_time_threshold_logging_service");
        frameCycleTimeThreshold = robotConfiguration.getInt("global_timing", "frame_cycle_time_threshold_info_thread");

        dashboard.initialize();

        logger.debug("LoggingService started");
    }

    @Override
    public void runOneIteration() throws Exception {

        double frameStartTime = System.currentTimeMillis();

        for (String name : desiredLogs) {
            String type = name.substring(0, 4);
            switch (type) {
                case "ipn_":
                    dashboard.putNumber(name, sharedInputValues.getNumeric(name));
                    break;
                case "ipb_":
                    dashboard.putBoolean(name, sharedInputValues.getBoolean(name));
                    break;
                case "ipv_":
                    Map<String, Double> inputvector = sharedInputValues.getVector(name);
                    for (String key : inputvector.keySet()) {
                        dashboard.putNumber(key, inputvector.get(key));
                    }
                    break;
                case "ips_":
                    dashboard.putString(name, sharedInputValues.getString(name));
                    break;
                case "opn_":
                    dashboard.putNumber(name, (double) sharedOutputValues.getOutputNumericValue(name).get("value"));
                    break;
                case "opb_":
                    dashboard.putBoolean(name, sharedOutputValues.getBoolean(name));
                    break;
                default:
                    throw new RuntimeException("The value type could not be determed for '" + name + "'. Ensure that it follows naming convention and matches " +
                            "its name from its yaml file. ");
            }
        }


        //Check for auto selection
        if (dashboard.autoSelectionRisingEdge()) {
            dashboard.smartdashboardSetAuto();
        }


        // Check for delayed frames
        double currentTime = System.currentTimeMillis();
        double frameTime = currentTime - frameStartTime;
        double totalCycleTime = frameStartTime - previousTime;
        sharedInputValues.setNumeric("ipn_frame_time_logging_service", frameTime);
        sharedInputValues.setNumeric("ipn_frame_cycle_time_info_thread", totalCycleTime);
        if (frameTime > frameTimeThreshold) {
            logger.debug("********** Logging Service frame time = {}", frameTime);
        }
        if (totalCycleTime > frameCycleTimeThreshold) {
            logger.debug("********** Info thread frame cycle time = {}", totalCycleTime);
        }
        previousTime = frameStartTime;
    }

    @Override
    public void shutDown() throws Exception {

    }

    @Override
    public Scheduler scheduler() {
        return new Scheduler(1000 / 60);
    }
}
