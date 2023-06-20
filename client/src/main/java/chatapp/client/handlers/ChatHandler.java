package chatapp.client.handlers;

import chatapp.client.Client;
import chatapp.client.local.LocalStore;
import chatapp.common.encryption.*;
import chatapp.common.message.Message;
import chatapp.common.message.MessageBuilder;
import chatapp.common.message.MessageKind;
import com.google.gson.Gson;

import java.math.BigInteger;
import java.util.HashMap;

public class ChatHandler {
	private static ChatHandler instance;

	private Gson gson;
	private HashMap<String, RSAKey> pendingConnections;
	private  HashMap<String, IEncryptionManager> connections;

	private ChatHandler() {
		gson = new Gson();
		pendingConnections = new HashMap<>();
		connections = new HashMap<>();
	}

	/**
	 * Fragt eine neue Verbindung an, wenn noch keine besteht.
	 * Wenn eine Verbindung besteht, also die Schlüssel übergenen sind, wird dieser verwendet.
	 * @param partner
	 * @param method
	 */
	public void requestChat(String partner, EncryptionMethod method) {
		if(LocalStore.getInstance().contains(partner)) {
			IEncryptionManager encryptionManager = LocalStore.getInstance().loadEncryptionManager(partner);
			this.connections.put(partner, encryptionManager);

			var msg = new MessageBuilder()
					.setKind(MessageKind.ResumeChat)
					.setSender(Client.state.username)
					.setReceiver(partner)
					.build();
			Client.connectionHandler.send(msg);
			return;
		}

		var gen = new RSAGenerator(1024);
		gen.generateKeys();
		var data = gson.toJson(gen.getPublicKey());
		pendingConnections.put(partner, gen.getPrivateKey());

		var msg = new Message(MessageKind.ChatRequested, method, Client.state.username, partner, data);
		Client.connectionHandler.send(msg);
	}

	public void chatRequested(Message message) {
		if(connections.containsKey(message.sender)) {
			// TODO: error
			return;
		}

		if(pendingConnections.containsKey(message.sender)) {
			// TODO: already requested
			return;
		}

		String data = "";
		if(message.method == EncryptionMethod.Caesar) {
			var key = gson.fromJson(message.data, RSAKey.class);
			data = key.run("2").toString();
			newConnection(message.sender, new Caesar(2));
		}
		else {
			var gen = new RSAGenerator(1024);
			gen.generateKeys();
			data = gson.toJson(gen.getPublicKey());
			var publicKey = gson.fromJson(message.data, RSAKey.class);
			newConnection(message.sender, new RSAEncrypter(publicKey, gen.getPrivateKey()));
		}

		var msg = new Message(MessageKind.ChatAccepted, message.method, Client.state.username, message.sender, data);
		Client.connectionHandler.send(msg);

		System.out.println("chat request from " + message.sender);
	}

	public void resumeChat(Message message) {
		var encryptionManager = LocalStore.getInstance().loadEncryptionManager(message.sender);
		connections.put(message.sender, encryptionManager);
	}

	public void connectionAccepted(Message message) {
		var init = pendingConnections.get(message.sender);
		pendingConnections.remove(message.sender);

		switch(message.method) {
			case Caesar -> {
				var key = init.run(new BigInteger(message.data));
				System.out.println(key);
				newConnection(message.sender, new Caesar(Integer.parseInt(key)));
			}
			case RSA -> {
				var publicKey = gson.fromJson(message.data, RSAKey.class);
				newConnection(message.sender, new RSAEncrypter(publicKey, init));
			}
		}

	}

	/**
	 * fügt den Kontakt dem chat hinzu.
	 * Speichert die verschlüsselungsdaten lokal.
	 * @param username der Kontakt
	 * @param encryptionManager die verwendete Verschlüsselung.
	 */
	private void newConnection(String username, IEncryptionManager encryptionManager) {
		connections.put(username, encryptionManager);

		LocalStore
				.getInstance()
				.saveEncryptionManager(username, encryptionManager);
	}

	public void sendMessage(String message) {
		var encrypted = connections.get(Client.state.partner).encrypt(message);

		Client.connectionHandler.send(new Message(MessageKind.ChatMessage, Client.state.username, Client.state.partner, encrypted));
	}

	public void receiveMessage(Message message) {
		var text = connections.get(message.sender).decrypt(message.data);
		System.out.println(message.sender + ": " + text);
	}

	public boolean setChat(String username) {
		if(!connections.containsKey(username)) {
			if(LocalStore.getInstance().contains(username)) {
				var encryptionManager = LocalStore.getInstance().loadEncryptionManager(username);
				connections.put(username, encryptionManager);

				var msg = new MessageBuilder()
						.setKind(MessageKind.ResumeChat)
						.setSender(Client.state.username)
						.setReceiver(username)
						.build();
				Client.connectionHandler.send(msg);
			}
			else {
				return false;
			}
		}
		Client.state.partner = username;
		return true;
	}

	public static ChatHandler getInstance() {
		if(instance == null) {
			instance = new ChatHandler();
		}
		return instance;
	}
}
