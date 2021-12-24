package org.uacr.services.webdashboard;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.uacr.utilities.RobotSystem;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import static org.uacr.utilities.RobotSystem.RuntimeMode.SIM_MODE;

public class WebHttpServer {

	@Nullable
	private HttpServer httpServer;

	public WebHttpServer(int port) {
		try {
			httpServer = HttpServer.create(new InetSocketAddress(port), 8);

			httpServer.createContext("/", (e) -> {
				fullWrite(e, readFile("webdashboard/webdashboard.html"));

				e.close();
			});

			httpServer.createContext("/pages", (e) -> {
				if (e.getRequestURI().getPath().contains("css") || e.getRequestURI().getPath().contains("ttf")) {
					e.getResponseHeaders().add("content-type", "text/css");
				}
				if (e.getRequestURI().getPath().contains("js")) {
					e.getResponseHeaders().add("content-type", "text/javascript");
				}
				e.getResponseHeaders().add("x-frame-options", "SAMEORIGIN");

				fullWrite(e, readFile(e.getRequestURI().getPath().split("/pages/", 2)[1]));

				e.close();
			});

			httpServer.createContext("/match", (e) -> {
				fullWrite(e, readFile("webdashboard/match/match.html"));

				e.close();
			});

			httpServer.createContext("/log", (e) -> {
				fullWrite(e, readFile("webdashboard/log/log.html"));

				e.close();
			});

			httpServer.createContext("/sim", (e) -> {
				fullWrite(e, readFile("webdashboard/sim/sim.html"));

				e.close();
			});

			httpServer.createContext("/quitquitquit", (e) -> {
				if (RobotSystem.getRuntimeMode() == SIM_MODE) {
					fullWrite(e, "kthxbye - killing sim");
					e.close();
					System.exit(0);
				} else {
					fullWrite(e, "srynope - cannot force quite in hardware mode");
					e.close();
				}
			});

			httpServer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//Reads entire file
	private static byte[] readFile(String path) {
		try {
			@Nullable
			InputStream fis = WebHttpServer.class.getClassLoader().getResourceAsStream(path);
			byte[] r = new byte[0];
			if (fis != null) {
				r = fis.readAllBytes();
				fis.read(r);
				fis.close();
			}

			return r;
		} catch (Exception e) {

		}

		return new byte[0];
	}

	//Writes entire file to HttpExchange
	private static void fullWrite(HttpExchange e, String r) {
		try {
			byte[] bytes = r.getBytes();
			e.sendResponseHeaders(200, bytes.length);
			OutputStream body = e.getResponseBody();
			body.write(bytes);
			body.flush();
			body.close();
		} catch (Exception e1) {

		}
	}

	//Writes entire file to HttpExchange
	private static void fullWrite(HttpExchange e, byte[] r) {
		fullWrite(e, new String(r));
	}
}
