package chatapp.server.models;

import chatapp.common.message.Message;
import chatapp.common.message.MessageBuilder;
import chatapp.common.message.MessageKind;

/**
 * Die Verschlüsselung wurde bereits beim Benutzer gespeichert.
 */
public class MessageModel {
	public  String sender;
	public String receiver;
	public String message;
	public long time;

	public MessageModel(String sender, String receiver, String message) {
		this.sender = "user:" + sender;
		this.receiver = "user:" + receiver;
		this.message = message;
		this.time = System.currentTimeMillis();
	}

	public Message toMessage() {
		return new MessageBuilder()
				.setKind(MessageKind.ChatMessage)
				.setSender(sender.replace("user:", ""))
				.setReceiver(receiver.replace("user:", ""))
				.setData(message)
				.build();
	}
}
