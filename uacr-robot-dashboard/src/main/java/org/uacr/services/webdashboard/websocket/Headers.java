package org.uacr.services.webdashboard.websocket;

import java.util.HashMap;

public class Headers extends HashMap<String, String> {

	private final String line;

	public Headers(String line) {
		this.line = line;
	}

	public Headers() {
		this("");
	}

	public void putHeader(String line) {
		String[] parts = line.split(": ");
		if (parts.length < 2) return;
		put(parts[0], parts[1]);
	}

	public String getHeaderText() {
		StringBuilder headerText = new StringBuilder();

		if (!line.equals("")) {
			headerText.append(line).append("\r\n");
		}

		for (Entry<String, String> header : this.entrySet()) {
			headerText.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
		}

		headerText.append("\r\n");

		return headerText.toString();
	}
}
