package chatapp.common.message;

import chatapp.common.encryption.EncryptionMethod;

public class MessageBuilder {
	private MessageKind kind;
	private EncryptionMethod method;
	private String sender;
	private String receiver;
	private String data;

	public MessageBuilder() {}

	public MessageBuilder setKind(MessageKind kind) {
		this.kind = kind;
		return this;
	}

	public MessageBuilder setMethod(EncryptionMethod method) {
		this.method = method;
		return this;
	}

	public MessageBuilder setSender(String sender) {
		this.sender = sender;
		return this;
	}

	public MessageBuilder setReceiver(String receiver) {
		this.receiver = receiver;
		return this;
	}

	public MessageBuilder setData(String data) {
		this.data = data;
		return this;
	}

	public Message build() {
		return new Message(kind, method, sender, receiver, data);
	}
}
