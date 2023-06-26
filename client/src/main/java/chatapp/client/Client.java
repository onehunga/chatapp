package chatapp.client;

import chatapp.client.handlers.ConnectionHandler;
import chatapp.client.handlers.InputHandler;

import java.io.IOException;
import java.net.ConnectException;

public class Client {
	public static State state;
	public static ConnectionHandler connectionHandler;

	public static void main(String[] args) {
		state = new State();

		new InputHandler().run();
	}

	public static void establishConnection() {
		try {
			connectionHandler = new ConnectionHandler("localhost", 1234);
			connectionHandler.startInput();
		} catch(IOException e) {
			if(e instanceof ConnectException) {
				System.out.println("failed to establish connection");
				state.loggedIn = false;
				return;
			}
			e.printStackTrace();
		}
	}

	public static void disconnect() {
		connectionHandler.disconnect();
		state.loggedIn = false;
	}

	public static void logout() {
		connectionHandler.disconnect();
		state = new State();
		connectionHandler = null;
	}
}
