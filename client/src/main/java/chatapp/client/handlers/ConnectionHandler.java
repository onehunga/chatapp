package chatapp.client.handlers;

import chatapp.client.Client;
import chatapp.common.message.Message;
import chatapp.common.message.MessageKind;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;

/**
 * Die Klasse behandelt die Verbindung zwischen den Klienten und dem Server.
 */
public class ConnectionHandler {
	private final Socket socket;
	private final BufferedWriter writer;
	private final MessageHandler messageHandler;
	private Thread messageThread;

	private final Gson gson = new Gson();

	/**
	 * @param host ist für dieses Projekt einfach "localhost".
	 * @param port der Port auf dem der Server läuft.
	 * @throws IOException
	 */
	public ConnectionHandler(String host, int port) throws IOException {
		this.socket = new Socket(host, port);

		this.writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
		var reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		this.messageHandler = new MessageHandler(reader);
		this.messageThread = new Thread(messageHandler);

		send(new Message(MessageKind.Login, Client.state.username));
	}

	public void startInput() {
		this.messageThread.start();
	}

	public void disconnect() {
		send(new Message(MessageKind.Disconnect, Client.state.username));
		close();
	}

	private void close() {
		this.messageHandler.close();
		try {
			writer.close();
			socket.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void send(Message message) {
		var msg = gson.toJson(message);

		try {
			writer.write(msg);
			writer.newLine();
			writer.flush();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
