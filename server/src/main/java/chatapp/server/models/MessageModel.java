package chatapp.server.models;

/**
 * Die Verschlüsselung wurde bereits beim Benutzer gespeichert.
 */
public class MessageModel {
	public  String sender;
	public String receiver;
	public String message;
	public long time;

	public MessageModel(String sender, String receiver, String message) {
		this.sender = sender;
		this.receiver = receiver;
		this.message = message;
		this.time = System.currentTimeMillis();
	}
}
