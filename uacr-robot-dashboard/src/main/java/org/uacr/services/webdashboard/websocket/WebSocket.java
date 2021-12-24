package org.uacr.services.webdashboard.websocket;

import org.uacr.utilities.LimitedSizeQueue;

import javax.annotation.Nullable;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class WebSocket {

	private final Socket socket;
	private final InputStream input;
	private final BufferedReader reader;
	private final OutputStream output;
	private final InetAddress address;
	private final AbstractWebsocketServer server;
	@Nullable
	private String path;
	@Nullable
	private Headers headers;
	private LimitedSizeQueue<String> sendQueue;

	protected WebSocket(Socket socket, AbstractWebsocketServer server) throws Exception {
		this.socket = socket;
		socket.setSoTimeout(1);

		input = socket.getInputStream();
		reader = new BufferedReader(new InputStreamReader(input));
		output = socket.getOutputStream();
		address = socket.getInetAddress();
		sendQueue = new LimitedSizeQueue<>(1000);

		this.server = server;

		handshake();

		server.onopen(this);
	}

	private void handshake() throws Exception {
		while (!reader.ready()) ;

		headers = new Headers(reader.readLine().trim());

		while (reader.ready()) {
			String line = reader.readLine().trim();
			headers.putHeader(line);
		}

		if (headers.containsKey("Sec-WebSocket-Key")) {
			Headers responseHeaders = new Headers("HTTP/1.1 101 Switching Protocols");

			responseHeaders.put("Connection", "Upgrade");
			responseHeaders.put("Upgrade", "websocket");
			responseHeaders.put("Sec-WebSocket-Accept", Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((headers.get("Sec-WebSocket-Key") + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes(StandardCharsets.UTF_8))));

			write(responseHeaders.getHeaderText().getBytes());
		}

		path = headers.getHeaderText().split(" ")[1];
	}

	public void send(String message) {
		sendQueue.add(message);
	}

	private void writeMessage(String message) {
		byte[] messageBytes = message.getBytes();

		byte[] bytes;

		if (message.length() < 126) {
			bytes = new byte[2 + message.length()];

			bytes[0] = (byte) 129;
			bytes[1] = (byte) message.length();

			for (int b = 0; b < messageBytes.length; b++) {
				bytes[b + 2] = messageBytes[b];
			}
		} else {
			bytes = new byte[4 + message.length()];

			bytes[0] = (byte) 129;
			bytes[1] = (byte) 126;

			bytes[2] = (byte) ((message.length() >> 8) & 0xFF);
			bytes[3] = (byte) (message.length() & 0xFF);

			for (int b = 0; b < messageBytes.length; b++) {
				bytes[b + 4] = messageBytes[b];
			}
		}

		try {
			write(bytes);
		} catch (Exception e) {
		}
	}

	public String getPath() {
		if (path == null) {
			return "";
		}
		return path;
	}

	private void write(byte[] message) throws IOException {
		output.write(message);
		output.flush();
	}

	protected void update() {
		try {
			while (!sendQueue.isEmpty()) {
				writeMessage(sendQueue.remove());
			}

			@Nullable
			String message = null;

			do {
				message = read();

				if (message != null) {
					if (message.equals("keepalive")) {
						writeMessage("keepalive");
						return;
					}

					for (byte b : message.getBytes()) {
						if (b < 0) {
							server.onclose(this);
							return;
						}
					}

					server.onmessage(this, message);
				}
			} while (message != null);
		} catch (Exception e) {
			server.onclose(this);
		}
	}

	protected void close() {
		byte[] bytes = new byte[]{(byte) 1000, (byte) 0};

		try {
			write(bytes);
		} catch (IOException e) {

		}
	}

	@Nullable
	private String read() throws Exception {
		try {
			byte[] control = new byte[2];

			input.read(control);

			int textLength = (control[1] & 0xFF) - 128;

			if (textLength > 125) {
				byte[] length = new byte[2];
				input.read(length);
				textLength = ((length[0] & 0xFF) << 8) | (length[1] & 0xFF);
			}

			byte[] key = new byte[4];
			input.read(key);

			byte[] encoded = new byte[textLength];
			input.read(encoded);

			byte[] decoded = new byte[textLength];
			for (int i = 0; i < textLength; i++) {
				decoded[i] = (byte) (encoded[i] ^ key[i & 0x3]);
			}

			return new String(decoded);

		} catch (SocketTimeoutException e) {

		} catch (Exception e) {
			throw e;
		}

		return null;
	}
}