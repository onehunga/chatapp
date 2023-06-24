package chatapp.server.handlers;

import chatapp.common.message.Message;
import chatapp.common.message.MessageBuilder;
import chatapp.common.message.MessageKind;
import chatapp.server.Database;
import chatapp.server.Server;
import chatapp.server.models.MessageModel;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler implements Runnable {
	private final Socket socket;
	private final BufferedWriter writer;
	private final BufferedReader reader;
	private String username;

	public ClientHandler(Socket socket) throws IOException {
		this.socket = socket;

		this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	@Override
	public void run() {
		try {
			String message;
			while(socket.isConnected()) {
				while(!reader.ready()) { Thread.sleep(200); }
				message = reader.readLine();
				onMessage(message);
			}
		} catch(IOException e) {
			if(e.getMessage().equals("Stream closed")) {
				return;
			}
			e.printStackTrace();
		} catch(InterruptedException e) {
			e.printStackTrace(); // es währe ein Wunder, wenn dieser Fall eintritt.
		}

		// wenn der socket nicht mehr verbunden ist
		Server.server.disconnect(username);
	}

	/**
	 * Da jeder neue Socket beim verbinden den Nutzernamen überträgt, wird dieser ausgelesen bevor es zur Lese schleife geht.
	 * @return Benutzername
	 */
	public String readLogin() {
		try {
			var raw = this.reader.readLine();
			System.out.println(raw);
			Message message = Server.gson.fromJson(raw, Message.class);
			this.username = message.sender;
			return message.sender;
		} catch(IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void send(Message message) {
		var msg = Server.gson.toJson(message);
		try {
			this.writer.write(msg);
			this.writer.newLine();
			this.writer.flush();
		} catch(SocketException e) {
			if(e.getMessage().equals("Connection reset by peer")) {
				Server.server.disconnect(username);
			} else {
				e.printStackTrace();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param rawMessage Die Nachricht im JSON Format
	 */
	private void onMessage(String rawMessage) {
		System.out.println(rawMessage);

		Message message = Server.gson.fromJson(rawMessage, Message.class);

		switch(message.kind) {
			case Disconnect -> Server.server.disconnect(message.sender);
			case ChatRequested, ChatAccepted -> {
				if(Server.server.handlers.containsKey(message.receiver)) {
					Server.server.handlers.get(message.receiver).send(message);
				}
				else if(message.kind == MessageKind.ChatRequested){
					var msg = new MessageBuilder()
							.setKind(MessageKind.ServerError)
							.setData("der Benutzer " + message.receiver + " konnte nicht gefunden werden")
							.build();

					send(msg);
				}
			}
			case ChatMessage -> {
				if(Server.server.handlers.containsKey(message.receiver)) {
					Server.server.handlers.get(message.receiver).send(message);
				}
				Database
						.getInstance()
						.insertMessage(new MessageModel(message.sender, message.receiver, message.data));
			}
		}
	}

	public void close() {
		try {
			if(this.writer != null) this.writer.close();
			if(this.reader != null) this.reader.close();
			if(this.socket != null) this.socket.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
