package chatapp.server;

import chatapp.common.logger.Logger;
import chatapp.server.handlers.ClientHandler;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Hauptklasse der Servers.
 */
public class Server implements Runnable {
	public static Server server;
	public static Logger logger;
	public static Gson gson;
	private final ServerSocket serverSocket;
	public final HashMap<String, ClientHandler> handlers;

	public Server(int port) throws IOException {
		this.serverSocket = new ServerSocket(port);
		this.handlers = new HashMap<>();
	}

	@Override
	public void run() {
		try {
			while(!serverSocket.isClosed()) {
				var socket = serverSocket.accept();
				handleConnection(socket);
			}
		} catch (IOException e) {
			if(e.getMessage().equals("Socket closed")) {
				return;
			}
			e.printStackTrace();
		}
	}

	private void handleConnection(Socket socket) throws IOException {
		var handler = new ClientHandler(socket);
		var username = handler.readLogin();

		if(this.handlers.containsKey(username)) {
			// TODO: reject login, because username exists
			return;
		}

		new Thread(handler).start();
		this.handlers.put(username, handler);
	}

	public void disconnect(String username) {
		var socket = this.handlers.get(username);
		socket.close();
		this.handlers.remove(username, socket);
	}

	public void close() {
		this.handlers.values().forEach(ClientHandler::close);
		try {
			this.serverSocket.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		logger = new Logger();
		gson = new Gson();

		server = new Server(1234);
		var serverThread = new Thread(server);
		serverThread.start();

		// Warte auf Konsoleneingabe um den Server zu schließen.
		var scanner = new Scanner(System.in);
		scanner.nextLine();
		server.close();
		Database.getInstance().disconnect();
	}
}
