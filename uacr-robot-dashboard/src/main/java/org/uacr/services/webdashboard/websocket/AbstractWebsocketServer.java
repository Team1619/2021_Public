package org.uacr.services.webdashboard.websocket;

import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

import javax.annotation.Nullable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractWebsocketServer {

	private static final Logger logger = LogManager.getLogger(AbstractWebsocketServer.class);

	private final int port;
	private final Set<WebSocket> sockets;
	private final Set<WebSocket> currentSockets;
	@Nullable
	private ServerSocket server;
	//private Executor executor;

	public AbstractWebsocketServer(int port) {
		this.port = port;
		sockets = Collections.synchronizedSet(new HashSet<>());
		currentSockets = Collections.synchronizedSet(new HashSet<>());
		//executor = Executors.newCachedThreadPool();
	}

	public AbstractWebsocketServer() {
		this(80);
	}

	public void start() {
		startServer();

		execute(this::onStart);

		run();
	}

	private void startServer() {
		try {
			server = new ServerSocket(port);
			server.setReuseAddress(true);
			server.setSoTimeout(1);
		} catch (Exception e) {
			onError(null, e);
		}
	}

	private void run() {
		execute(() -> {
			Thread.currentThread().setName("WebSocketServer - Run");
			while (!Thread.currentThread().isInterrupted()) {
				try {
					@Nullable
					Socket connection = null;
					if (server != null) {
						connection = server.accept();
					}

					try {
						if (connection != null) {
							new WebSocket(connection, this);
						}
					} catch (Exception e) {
						execute(() -> onError(null, e));
					}
				} catch (SocketTimeoutException e) {

				} catch (Exception e) {
					execute(() -> onError(null, e));
					startServer();
				}

				try {
					currentSockets.clear();
					currentSockets.addAll(sockets);

					for (WebSocket socket : currentSockets) {
						try {
							socket.update();
						} catch (Exception e) {
							execute(() -> onError(socket, e));
						}
					}
				} catch (Exception e) {
					execute(() -> onError(null, e));
				}

				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
				}
			}

			currentSockets.clear();
			currentSockets.addAll(sockets);

			for (WebSocket socket : currentSockets) {
				socket.close();
			}

			logger.debug("Websocket server shutting down");
		});
	}

	protected void execute(Runnable run) {
		new Thread(run).start();
		//executor.execute(run);
	}

	public abstract void onStart();

	protected final void onopen(WebSocket webSocket) {
		sockets.add(webSocket);
		onOpen(webSocket);
	}

	public abstract void onOpen(WebSocket webSocket);

	protected final void onmessage(WebSocket webSocket, String message) {
		onMessage(webSocket, message);
	}

	public abstract void onMessage(WebSocket webSocket, String message);

	protected final void onclose(WebSocket webSocket) {
		sockets.remove(webSocket);
		onClose(webSocket);
	}

	public abstract void onClose(WebSocket webSocket);

	protected final void onerror(WebSocket webSocket, Exception e) {
		onError(webSocket, e);
	}

	public abstract void onError(@Nullable WebSocket webSocket, Exception e);
}