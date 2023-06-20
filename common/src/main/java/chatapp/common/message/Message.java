package chatapp.common.message;

import chatapp.common.encryption.EncryptionMethod;

public class Message {
	public MessageKind kind;
	public EncryptionMethod method;
	public String sender;
	public String receiver;
	public String data;

	public Message(MessageKind kind, String sender) {
		this.kind = kind;
		this.sender = sender;
	}

	public Message(MessageKind kind, String sender, String receiver, String message) {
		this.kind = kind;
		this.sender = sender;
		this.receiver = receiver;
		this.data = message;
	}

	public Message(MessageKind kind, EncryptionMethod method, String sender, String receiver, String data) {
		this.kind = kind;
		this.method = method;
		this.sender = sender;
		this.receiver = receiver;
		this.data = data;
	}
}
