package chatapp.server;

import chatapp.server.handlers.ClientHandler;
import chatapp.server.models.MessageModel;
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

		connectionData(username);
	}

	private void connectionData(String username) {
		var handler = this.handlers.get(username);

		// lade alle alten nachrichten, falls forhanden
		var messageModels = Database.getInstance().getMessages(username);
		messageModels.stream().map(MessageModel::toMessage).forEach(handler::send);

		Database.getInstance().updateUserTime(username);
	}

	/**
	 * Entweder durch das logout Event vom Client oder bei Verbindungsabruch
	 * @param username
	 */
	public void disconnect(String username) {
		System.out.println("User: " + username + " disconnected");

		var socket = this.handlers.get(username);
		socket.close();
		this.handlers.remove(username, socket);

		Database.getInstance().updateUserTime(username);
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
