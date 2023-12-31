package chatapp.client.handlers;

import chatapp.client.Client;
import chatapp.common.message.Message;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;

public class MessageHandler implements Runnable {
	private final Gson gson;
	private final BufferedReader reader;
	private boolean open;
	private final ChatHandler chatHandler;

	public MessageHandler(BufferedReader reader) {
		this.reader = reader;
		this.open = true;
		this.gson = new Gson();

		this.chatHandler = ChatHandler.getInstance();
	}

	@Override
	public void run() {
		while(this.open) {
			try {
				var msg = this.readMessage();
				this.handleMessage(msg);
			} catch(SocketException e) {
				System.out.println("Verbindung verloren!");
				Client.disconnect();
				Client.state.appQuit = true;
			} catch(IOException e) {
				// Dieser Fehler ist am Ende des Projektes zu erwarten
				if(e.getMessage().equals("Stream closed")) {
					return;
				}
				e.printStackTrace();
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private Message readMessage() throws InterruptedException, IOException {
		while(!reader.ready()) {
			Thread.sleep(200);
		}

		String line = reader.readLine();

		return gson.fromJson(line, Message.class);
	}

	private void handleMessage(Message message) {
		switch (message.kind) {
			case ChatRequested -> chatHandler.chatRequested(message);
			case ChatAccepted -> chatHandler.connectionAccepted(message);
			case ChatMessage -> chatHandler.receiveMessage(message);
			case ServerError -> {
				System.out.println("Fehler: " + message.data);
			}
			case LoginFailedError -> {
				System.out.println("Fehler: " + message.data);
				Client.logout();
				Client.state.loggedIn = false;
				Client.connectionHandler = null;
			}
		}
	}

	public void close() {
		this.open = false;
		try {
			this.reader.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
