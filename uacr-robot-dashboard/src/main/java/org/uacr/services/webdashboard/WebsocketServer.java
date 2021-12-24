package org.uacr.services.webdashboard;

import org.uacr.events.sim.SimInputBooleanSetEvent;
import org.uacr.events.sim.SimInputNumericSetEvent;
import org.uacr.events.sim.SimInputVectorSetEvent;
import org.uacr.services.webdashboard.websocket.AbstractWebsocketServer;
import org.uacr.services.webdashboard.websocket.WebSocket;
import org.uacr.shared.abstractions.*;
import org.uacr.utilities.LimitedSizeQueue;
import org.uacr.utilities.logging.LogHandler;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;
import org.uacr.utilities.services.Scheduler;

import javax.annotation.Nullable;
import java.util.*;

/**
 * WebsocketServer connects and communicates with the computer webdashboard server
 *
 * @author Matthew Oates
 */

public class WebsocketServer extends AbstractWebsocketServer implements LogHandler {

	private static final Logger logger = LogManager.getLogger(WebsocketServer.class);
	private final EventBus eventBus;
	private final FMS fms;
	private final InputValues sharedInputValues;
	private final OutputValues sharedOutputValues;
	private final RobotConfiguration robotConfiguration;
	private final Set<WebSocket> webDashboardSockets = new HashSet<>();
	private final Set<WebSocket> valuesSockets = new HashSet<>();
	private final Set<WebSocket> matchSockets = new HashSet<>();

	//Web sockets
	//Connects web page
	private final Set<WebSocket> logSockets = new HashSet<>();
	private final Map<String, Object> lastMatchValues = new HashMap<>();
	private final Scheduler loggingScheduler = new Scheduler(250);
	private List<String> autoOriginList = new ArrayList<>();
	private List<String> autoDestinationList = new ArrayList<>();
	private List<String> autoActionList = new ArrayList<>();
	private Map<String, Double> numerics = new HashMap<>();
	private Map<String, Boolean> booleans = new HashMap<>();
	private Map<String, String> strings = new HashMap<>();
	private Map<String, Map<String, Double>> vectors = new HashMap<>();
	private Map<String, Object> outputs = new HashMap<>();
	private Map<String, Double> lastNumerics = new HashMap<>();
	private Map<String, Boolean> lastBooleans = new HashMap<>();
	private Map<String, String> lastStrings = new HashMap<>();
	private Map<String, Map<String, Double>> lastVectors = new HashMap<>();
	private Map<String, Object> lastOutputs = new HashMap<>();
	private Map<String, Map<String, Object>> matchValues = new HashMap<>();
	private Queue<Map<String, String>> logMessages = new LimitedSizeQueue<>(100);
	private Queue<Map<String, String>> webdashboadLogMessages = new LimitedSizeQueue<>(100);
	private StringBuilder mainStringBuilder = new StringBuilder();
	private StringBuilder secondaryStringBuilder = new StringBuilder();
	private UrlFormData sendFormData = new UrlFormData();
	private UrlFormData receiveFormData = new UrlFormData();
	private Map<String, Object> allMatchValues = new HashMap<>();

	public WebsocketServer(int port, EventBus eventBus, FMS fms, InputValues inputValues, OutputValues outputValues, RobotConfiguration robotConfiguration) {
		super(port);

		this.eventBus = eventBus;
		this.fms = fms;
		sharedInputValues = inputValues;
		sharedOutputValues = outputValues;
		this.robotConfiguration = robotConfiguration;

		sharedInputValues.setString("ips_selected_auto", "No Auto");

		LogManager.addLogHandler(this);
	}

	public void initialize() {
		Map<String, Object> config = robotConfiguration.getCategory("global_webdashboard");

		Object matchValuesConfig = config.get("match_values");
		if (matchValuesConfig instanceof HashMap) {
			matchValues = (Map<String, Map<String, Object>>) matchValuesConfig;
		}

		Object autoSelectorObject = config.get("auto_selector");
		if (autoSelectorObject instanceof Map) {
			Map<String, List<String>> autoSelector = (Map<String, List<String>>) autoSelectorObject;

			autoOriginList = autoSelector.get("origins");
			autoDestinationList = autoSelector.get("destinations");
			autoActionList = autoSelector.get("actions");
		}

		start();
	}

	//Puts a log message into the cue to be sent to the dashboard
	public void log(String type, String message) {
		logMessages.add(Map.of("type", type, "message", message));
		webdashboadLogMessages.add(Map.of("type", type, "message", message));
	}

	//Called by the service every frame
	public void broadcastToWebDashboard() {
		broadcastValuesDataToWebDashboard();

		broadcastMatchDataToWebDashboard();

		broadcastLogDataToWebDashboard();
	}

	//Send information for the values page
	private void broadcastValuesDataToWebDashboard() {
		if (valuesSockets.isEmpty()) return;

		mainStringBuilder.setLength(0);

		numerics.clear();
		numerics.putAll(sharedInputValues.getAllNumerics());
		for (HashMap.Entry<String, Double> value : numerics.entrySet()) {
			if (!value.getValue().equals(lastNumerics.get(value.getKey()))) {
				mainStringBuilder.append("numeric*").append(value.getKey()).append("*").append(String.format("%6f", value.getValue())).append("~");
			}
		}
		lastNumerics.clear();
		lastNumerics.putAll(numerics);

		booleans.clear();
		booleans.putAll(sharedInputValues.getAllBooleans());
		for (HashMap.Entry<String, Boolean> value : booleans.entrySet()) {
			if (!value.getValue().equals(lastBooleans.get(value.getKey()))) {
				mainStringBuilder.append("boolean*").append(value.getKey()).append("*").append(value.getValue()).append("~");
			}
		}
		lastBooleans.clear();
		lastBooleans.putAll(booleans);

		strings.clear();
		strings.putAll(sharedInputValues.getAllStrings());
		for (HashMap.Entry<String, String> value : strings.entrySet()) {
			if (!value.getValue().equals(lastStrings.get(value.getKey()))) {
				mainStringBuilder.append("string*").append(value.getKey()).append("*").append(value.getValue()).append("~");
			}
		}
		lastStrings.clear();
		lastStrings.putAll(strings);

		vectors.clear();
		vectors.putAll(sharedInputValues.getAllVectors());
		for (HashMap.Entry<String, Map<String, Double>> value : vectors.entrySet()) {
			if (!value.getValue().equals(lastVectors.get(value.getKey()))) {
				mainStringBuilder.append("vector*").append(value.getKey());
				for (Map.Entry<String, Double> v : value.getValue().entrySet()) {
					mainStringBuilder.append("*").append(v.getKey()).append(": ").append(v.getValue());
				}
				mainStringBuilder.append("~");
			}
		}
		lastVectors.clear();
		lastVectors.putAll(vectors);

		outputs.clear();
		outputs.putAll(sharedOutputValues.getAllOutputs());
		for (HashMap.Entry<String, Object> value : outputs.entrySet()) {
			if (!value.getValue().equals(lastOutputs.get(value.getKey()))) {
				mainStringBuilder.append("output*").append(value.getKey()).append("*").append(value.getValue()).append("~");
			}
		}
		lastOutputs.clear();
		lastOutputs.putAll(outputs);

		if (mainStringBuilder.length() > 0) {

			sendFormData.clear();

			send(valuesSockets, sendFormData
					.add("response", "values")
					.add("values", mainStringBuilder.substring(0, mainStringBuilder.length() - 1))
					.getData());
		}
	}

	//Send information for the match web page
	private void broadcastMatchDataToWebDashboard() {
		if (matchSockets.isEmpty()) return;

		allMatchValues.clear();
		allMatchValues.putAll(sharedInputValues.getAllNumerics());
		allMatchValues.putAll(sharedInputValues.getAllBooleans());
		allMatchValues.putAll(sharedInputValues.getAllStrings());
		allMatchValues.putAll(sharedOutputValues.getAllOutputs());

		mainStringBuilder.setLength(0);

		for (HashMap.Entry<String, Map<String, Object>> matchValue : matchValues.entrySet()) {
			String type = matchValue.getValue().get("type").toString();

			String name = matchValue.getKey();
			if (matchValue.getValue().containsKey("display_name")) {
				name = String.valueOf(matchValue.getValue().get("display_name"));
			}

			String value = "";
			if (allMatchValues.containsKey(matchValue.getKey())) {
				value = String.valueOf(allMatchValues.get(matchValue.getKey()));
			} else {
				if (type.equals("value") || type.equals("boolean") || type.equals("other") || type.equals("auto")) {
					value = "";
				} else if (type.equals("dial")) {
					value = "0";
				}
			}

			if (!(lastMatchValues.containsKey(matchValue.getKey()) && lastMatchValues.get(matchValue.getKey()).equals(value))) {
				if (type.equals("value") || type.equals("boolean") || type.equals("other") || type.equals("auto")) {
					mainStringBuilder.append(type).append("*$#$*").append(name).append("*$#$*").append(value).append("~$#$~");
				} else if (type.equals("dial")) {
					String min = "0";
					if (matchValue.getValue().containsKey("min")) {
						min = String.valueOf(matchValue.getValue().get("min"));
					}

					String max = "10";
					if (matchValue.getValue().containsKey("max")) {
						max = String.valueOf(matchValue.getValue().get("max"));
					}

					mainStringBuilder.append(type).append("*$#$*").append(name).append("*$#$*").append(value).append("*$#$*").append(min).append("*$#$*").append(max).append("~$#$~");
				} else if (type.equals("log")) {
					String level = "INFO";
					if (matchValue.getValue().containsKey("level")) {
						level = String.valueOf(matchValue.getValue().get("level")).toUpperCase();
					}

					secondaryStringBuilder.setLength(0);

					while (!webdashboadLogMessages.isEmpty()) {
						Map<String, String> data = webdashboadLogMessages.remove();

						if (LogManager.Level.valueOf(data.get("type")).getPriority() >= LogManager.Level.valueOf(level).getPriority()) {
							secondaryStringBuilder.append("TYPE:").append(data.get("type")).append("MESSAGE:").append(data.get("message"));
						}
					}

					if (secondaryStringBuilder.length() > 0) {
						value = secondaryStringBuilder.toString();

						mainStringBuilder.append(type).append("*$#$*").append(name).append("*$#$*").append(secondaryStringBuilder).append("~$#$~");
					} else {
						value = "empty";
					}
				}
			}

			lastMatchValues.put(matchValue.getKey(), value);
		}

		sendFormData.clear();

		if (mainStringBuilder.length() > 0) {
			send(matchSockets, sendFormData
					.add("response", "match_values")
					.add("values", mainStringBuilder.substring(0, mainStringBuilder.length() - 5))
					.getData());
		}
	}

	//Sends information for the log web page
	private void broadcastLogDataToWebDashboard() {

		if (loggingScheduler.shouldRun()) {
			loggingScheduler.run();

			if (logSockets.isEmpty()) {
				return;
			}

			mainStringBuilder.setLength(0);

			while (!logMessages.isEmpty()) {
				Map<String, String> data = logMessages.remove();

				mainStringBuilder.append("TYPE:").append(data.get("type")).append("MESSAGE:").append(data.get("message"));
			}

			sendFormData.clear();

			if (mainStringBuilder.length() > 0) {
				send(logSockets, sendFormData
						.add("response", "log")
						.add("messages", mainStringBuilder.toString())
						.getData());
			}
		}
	}

	private String listToUrlFormDataList(List list) {
		StringBuilder urlFormDataList = new StringBuilder();

		for (Object item : list) {
			urlFormDataList.append(item.toString()).append("~");
		}
		if (urlFormDataList.length() > 0) {
			urlFormDataList = new StringBuilder(urlFormDataList.substring(0, urlFormDataList.length() - 1));
		}

		return urlFormDataList.toString();
	}

	//Called when a new websocket connection opens
	@Override
	public void onOpen(WebSocket socket) {
		try {
			switch (socket.getPath()) {
				case "/webdashboard": {
					webDashboardSockets.add(socket);

					sendAutoData();

					sendConnected();

					break;
				}
				case "/values": {
					valuesSockets.add(socket);

					clearAllValues();

					break;
				}
				case "/match": {
					matchSockets.add(socket);

					sendAutoData();

					clearMatchValues();
					break;
				}
				case "/log": {
					logSockets.add(socket);
					break;
				}
			}
		} catch (Exception e) {
			onError(socket, e);
		}
	}

	//Called when a websocket connection closes
	@Override
	public void onClose(WebSocket webSocket) {
		removeSocket(webSocket);
	}

	//All messages received over websocket connections come here to be forwarded to the robot or web page
	@Override
	public void onMessage(WebSocket webSocket, String message) {

		receiveFormData.clear();
		receiveFormData.parse(message);

		if (receiveFormData.containsKey("request")) {
			switch (receiveFormData.get("request")) {
				case "all_values": {
					clearAllValues();
					break;
				}
				case "all_match_values": {
					clearMatchValues();
					break;
				}
				case "change_value": {
					switch (receiveFormData.get("type")) {
						case "numeric":
							eventBus.post(new SimInputNumericSetEvent(receiveFormData.get("name"), Double.valueOf(receiveFormData.get("value"))));
							break;
						case "boolean":
							eventBus.post(new SimInputBooleanSetEvent(receiveFormData.get("name"), Boolean.valueOf(receiveFormData.get("value"))));
							break;
						case "string":
							break;
						case "vector":
							Map<String, Double> vector = new HashMap<>();
							vector.putAll(sharedInputValues.getVector(receiveFormData.get("name")));
							vector.put(receiveFormData.get("selected"), Double.valueOf(receiveFormData.get("value")));
							eventBus.post(new SimInputVectorSetEvent(receiveFormData.get("name"), vector));
							break;
					}
					break;
				}
				case "get_auto_data": {
					sendAutoData();
					break;
				}
				case "set_auto_data": {
					sharedInputValues.setString("ips_auto_origin", receiveFormData.get("auto_origin"));
					sharedInputValues.setString("ips_auto_destination", receiveFormData.get("auto_destination"));
					sharedInputValues.setString("ips_auto_action", receiveFormData.get("auto_action"));
					sharedInputValues.setString("ips_selected_auto",
							sharedInputValues.getString("ips_auto_origin") + ", " +
									sharedInputValues.getString("ips_auto_destination") + ", " +
									sharedInputValues.getString("ips_auto_action"));
					break;
				}
				case "set_fms_mode": {
					switch (receiveFormData.get("mode")) {
						case "auto":
							fms.setMode(FMS.Mode.AUTONOMOUS);
							return;
						case "teleop":
							fms.setMode(FMS.Mode.TELEOP);
							return;
						case "disabled":
							fms.setMode(FMS.Mode.DISABLED);
							return;
						case "test":
							fms.setMode(FMS.Mode.TEST);
							return;
					}
				}
				default: {
					logger.debug("Unknown data: " + receiveFormData);
				}
			}
		} else {
			logger.debug("Unknown message: " + message);
		}
	}

	//All websocket error are handled here
	@Override
	public void onError(@Nullable WebSocket webSocket, Exception e) {
		logger.error("{} exception on {}", e.getMessage(), webSocket);
		e.printStackTrace();
	}

	//Called when the server starts
	@Override
	public void onStart() {

	}

	//Sends robot connection status to the web page
	private synchronized void sendConnected() {
		String message = new UrlFormData()
				.add("response", "connected")
				.add("connected", "true")
				.getData();

		send(webDashboardSockets, message);
	}

	private void sendAutoData() {
		String response = new UrlFormData()
				.add("response", "auto_data")
				.add("auto_origin_list", listToUrlFormDataList(autoOriginList))
				.add("auto_destination_list", listToUrlFormDataList(autoDestinationList))
				.add("auto_action_list", listToUrlFormDataList(autoActionList))
				.getData();

		send(webDashboardSockets, response);
		send(matchSockets, response);
	}

	private void clearAllValues() {
		lastNumerics.clear();
		lastBooleans.clear();
		lastStrings.clear();
		lastVectors.clear();
		lastOutputs.clear();
	}

	private void clearMatchValues() {
		lastMatchValues.clear();
	}

	private void send(Set<WebSocket> sockets, String message) {
		sockets.forEach(socket -> {
			try {
				socket.send(message);
			} catch (Exception e) {
				logger.error(e);
			}
		});
	}

	private void removeSocket(WebSocket socket) {
		webDashboardSockets.remove(socket);
		valuesSockets.remove(socket);
		matchSockets.remove(socket);
		logSockets.remove(socket);
	}

	// Call the log method with the correct message level
	@Override
	public void trace(String message) {
		log("TRACE", message);
	}

	@Override
	public void debug(String message) {
		log("DEBUG", message);
	}

	@Override
	public void info(String message) {
		log("INFO", message);
	}

	@Override
	public void error(String message) {
		log("ERROR", message);
	}
}
