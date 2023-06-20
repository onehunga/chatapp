package chatapp.client.handlers;

import chatapp.client.Client;
import chatapp.common.message.Message;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.function.Function;

public class MessageHandler implements Runnable {
	private Gson gson;
	private BufferedReader reader;
	private boolean open;
	private ChatHandler chatHandler;

	public Function<Message, Void> handle = null;

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
				if(handle != null) {
					handle.apply(msg);
					handle = null;
				}
				this.handleMessage(msg);
			} catch(IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	 Message readMessage() throws InterruptedException, IOException {
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
			case ResumeChat -> chatHandler.resumeChat(message);
			case ChatMessage -> chatHandler.receiveMessage(message);
		}
	}

	public void next(Function<Message, Void> handle) {
		this.handle = handle;
	}

	public void close() {
		this.open = false;
	}
}