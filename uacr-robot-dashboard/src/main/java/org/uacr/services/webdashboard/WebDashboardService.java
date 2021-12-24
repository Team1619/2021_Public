package org.uacr.services.webdashboard;

import org.uacr.shared.abstractions.*;
import org.uacr.utilities.injection.Inject;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;
import org.uacr.utilities.services.ScheduledService;
import org.uacr.utilities.services.Scheduler;


/**
 * WebDashboardService is the service the creates and runs the webdashboard client in sim and on the robot
 *
 * @author Matthew Oates
 */

public class WebDashboardService implements ScheduledService {

	private static final Logger logger = LogManager.getLogger(WebDashboardService.class);

	private final InputValues sharedInputValues;
	private final RobotConfiguration robotConfiguration;
	private final WebDashboardServer webDashboardServer;
	private double previousTime;
	private long FRAME_TIME_THRESHOLD;

	@Inject
	public WebDashboardService(EventBus eventBus, FMS fms, InputValues inputValues, OutputValues outputValues, RobotConfiguration robotConfiguration) {
		sharedInputValues = inputValues;
		this.robotConfiguration = robotConfiguration;

		webDashboardServer = new WebDashboardServer(eventBus, fms, inputValues, outputValues, robotConfiguration);
	}

	@Override
	public void startUp() throws Exception {
		logger.debug("Starting WebDashboardService");

		previousTime = System.currentTimeMillis();
		FRAME_TIME_THRESHOLD = robotConfiguration.getInt("global_timing", "frame_time_threshold_webdashboard_service");

		webDashboardServer.start();
		logger.debug("WebDashboardService started");
	}

	@Override
	public void runOneIteration() throws Exception {

		double frameStartTime = System.currentTimeMillis();

		webDashboardServer.update();

		// Check for delayed frames
		double currentTime = System.currentTimeMillis();
		double frameTime = currentTime - frameStartTime;
		double totalCycleTime = currentTime - previousTime;
		sharedInputValues.setNumeric("ipn_frame_time_webdashboard_service", frameTime);
		if (frameTime > FRAME_TIME_THRESHOLD) {
			logger.debug("********** WebDashboard Service frame time = {}", frameTime);
		}
		previousTime = currentTime;
	}

	@Override
	public void shutDown() throws Exception {
		logger.debug("Shutting down RobotWebDashboardService");

		webDashboardServer.stop();

		logger.debug("WebDashboardService shut down");
	}

	@Override
	public Scheduler scheduler() {
		return new Scheduler(1000 / 60);
	}
}
