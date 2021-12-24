package org.uacr.services.webdashboard;

import org.uacr.shared.abstractions.*;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

/**
 * WebDashboardServer creates and manages a WebsocketServer on the correct ip
 *
 * @author Matthew Oates
 */

public class WebDashboardServer {

	private static final Logger logger = LogManager.getLogger(WebDashboardServer.class);
	private static final int port = 5800;

	private final WebHttpServer webHttpServer;
	private final WebsocketServer websocketServer;

	public WebDashboardServer(EventBus eventBus, FMS fms, InputValues inputValues, OutputValues outputValues, RobotConfiguration robotConfiguration) {
		webHttpServer = new WebHttpServer(port);
		websocketServer = new WebsocketServer(port + 1, eventBus, fms, inputValues, outputValues, robotConfiguration);
	}

	public void start() {
		websocketServer.initialize();
	}

	public void update() {
		websocketServer.broadcastToWebDashboard();
	}

	public void stop() {

	}
}
