package chatapp.client.handlers;

import chatapp.client.Client;
import chatapp.client.local.LocalStore;
import chatapp.common.encryption.*;
import chatapp.common.message.Message;
import chatapp.common.message.MessageKind;
import com.google.gson.Gson;

import java.math.BigInteger;
import java.util.HashMap;

public class ChatHandler {
	private static ChatHandler instance;

	private final Gson gson;
	private final HashMap<String, RSAKey> pendingConnections;
	private final HashMap<String, IEncryptionManager> connections;

	private ChatHandler() {
		gson = new Gson();
		pendingConnections = new HashMap<>();
		connections = new HashMap<>();
	}

	/**
	 * Fragt eine neue Verbindung an, wenn noch keine besteht.
	 * Wenn eine Verbindung besteht, also die Schlüssel übergenen sind, wird dieser verwendet.
	 * @param partner Benutzername des gewünschten chat partners
	 * @param method Gewünschte verschlüsselungsmethode
	 */
	public void requestChat(String partner, EncryptionMethod method) {
		if(LocalStore.getInstance().contains(partner)) {
			IEncryptionManager encryptionManager = LocalStore.getInstance().loadEncryptionManager(partner);
			this.connections.put(partner, encryptionManager);
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
			return;
		}
		if(pendingConnections.containsKey(message.sender)) {
			return;
		}

		String data;
		if(message.method == EncryptionMethod.Caesar) {
			var key = gson.fromJson(message.data, RSAKey.class);
			var handler = new Caesar();
			data = key.run(String.valueOf(handler.getKey())).toString();
			newConnection(message.sender, handler);
		}
		else {
			var gen = new RSAGenerator(1024);
			gen.generateKeys();
			data = gson.toJson(gen.getPublicKey());
			var publicKey = gson.fromJson(message.data, RSAKey.class);
			newConnection(message.sender, new RSA(publicKey, gen.getPrivateKey()));
		}

		var msg = new Message(MessageKind.ChatAccepted, message.method, Client.state.username, message.sender, data);
		Client.connectionHandler.send(msg);
	}

	public void connectionAccepted(Message message) {
		var init = pendingConnections.get(message.sender);
		pendingConnections.remove(message.sender);

		switch(message.method) {
			case Caesar -> {
				var key = init.run(new BigInteger(message.data));
				newConnection(message.sender, new Caesar(Integer.parseInt(key)));
			}
			case RSA -> {
				var publicKey = gson.fromJson(message.data, RSAKey.class);
				newConnection(message.sender, new RSA(publicKey, init));
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
		if(!connections.containsKey(Client.state.partner)) {
			return;
		}
		var encrypted = connections.get(Client.state.partner).encrypt(message);

		Client.connectionHandler.send(new Message(MessageKind.ChatMessage, Client.state.username, Client.state.partner, encrypted));
	}

	public void receiveMessage(Message message) {
		if(!connections.containsKey(message.sender)) {
			if(!loadContact(message.sender)) {
				return;
			}
		}
		var text = connections.get(message.sender).decrypt(message.data);
		System.out.println(message.sender + ": " + text);
	}

	public boolean setChat(String username) {
		if(!connections.containsKey(username)) {
			if(LocalStore.getInstance().contains(username)) {
				var encryptionManager = LocalStore.getInstance().loadEncryptionManager(username);
				connections.put(username, encryptionManager);
			}
			else {
				return false;
			}
		}
		Client.state.partner = username;
		return true;
	}

	private boolean loadContact(String username) {
		if(LocalStore.getInstance().contains(username)) {
			IEncryptionManager manager = LocalStore.getInstance().loadEncryptionManager(username);
			connections.put(username, manager);
			return true;
		}
		return false;
	}

	public static void reset() {
		instance = new ChatHandler();
	}

	public static ChatHandler getInstance() {
		if(instance == null) {
			instance = new ChatHandler();
		}
		return instance;
	}
}
