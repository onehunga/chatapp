package chatapp.client.handlers;

import chatapp.client.Client;
import chatapp.common.encryption.EncryptionMethod;

import java.util.Arrays;
import java.util.Scanner;

public class InputHandler {

	// Konstanten für die ausgabe von buntem Text
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";

	private final Scanner scanner;
	private final ChatHandler chatHandler;

	public InputHandler() {
		this.scanner = new Scanner(System.in);
		chatHandler = ChatHandler.getInstance();
	}

	public void run() {
		while(!Client.state.appQuit) {
			var line = this.scanner.nextLine();

			var command = line.split(" ");
			if(command[0].length() == 0) {
				System.out.printf("%sBitte gib ein Komando ein!%s\n", ANSI_RED, ANSI_RESET);
				continue;
			}

			switch(command[0]) {
				case "login" -> {
					if(Client.state.loggedIn) {
						System.out.printf("%sDas Login Komando ist nur verfuegbar wenn man nicht angemeldet ist%s\n", ANSI_RED, ANSI_RESET);
						break;
					}
					if(command.length != 2) {
						System.out.printf("%sDas Login Komando hat genau zwei Parameter%s\n", ANSI_RED, ANSI_RESET);
						break;
					}
					var username = command[1];
					Client.state.loggedIn = true;
					Client.state.username = username;
					Client.establishConnection();
				}
				case "logout" -> {
					if(!Client.state.loggedIn) {
						System.out.printf("%sDu bist nicht angemeldet!%s\n", ANSI_YELLOW, ANSI_RESET);
						break;
					}
					Client.logout();
				}
				case "send" -> {
					if(command.length == 1) {
						System.out.printf("%sBitte gib auch eine Nachricht an, die gesendet werden soll%s\n", ANSI_YELLOW, ANSI_RESET);
						break;
					}
					var list = new java.util.ArrayList<>(Arrays.stream(command).toList());
					list.remove(0);
					StringBuilder msgBuilder = new StringBuilder();
					for(var elem : list) {
						msgBuilder.append(elem).append(' ');
					}
					msgBuilder.deleteCharAt(msgBuilder.length() - 1);
					var msg = msgBuilder.toString();

					chatHandler.sendMessage(msg);
				}
				case "chat" -> {
					if(command.length == 1) {
						if(Client.state.partner == null) {
							System.out.printf("%sDu chattest gerade mit niemandem.%s\n", ANSI_GREEN, ANSI_RESET);
						}
						else {
							System.out.printf("%sDu chattest gerade mit %s.%s\n", ANSI_GREEN, Client.state.partner, ANSI_RESET);
						}
						break;
					}
					if(!Client.state.loggedIn) {
						System.out.printf("%sUm einen neuen chat zu beginnen musst du dich erst anmelden%s\n", ANSI_RED, ANSI_RESET);
						break;
					}

					if(command.length == 2) {
						var partner = command[1];
						if(!chatHandler.setChat(partner)) {
							System.out.printf("%sDer benutzer wurde nicht gefunden!%s\n", ANSI_YELLOW, ANSI_RESET);
						}
					}
					else if(command.length == 3) {
						var partner = command[1];
						var encryption = command[2];

						EncryptionMethod method;
						if(encryption.equalsIgnoreCase("rsa")) {
							method = EncryptionMethod.RSA;
						} else if(encryption.equalsIgnoreCase("caesar")) {
							method = EncryptionMethod.Caesar;
						} else {
							System.out.printf("%sDie Verschlüsselungsmethode muss rsa oder caesar sein!%s\n", ANSI_RED, ANSI_RESET);
							break;
						}

						ChatHandler.getInstance().requestChat(partner, method);
					}
					else {
						System.out.printf("%sUm einen neuen Chat anzufangen muss folgendes format verwendet werden 'chat <benutzer> <verschlüsselung>%s\n", ANSI_RED, ANSI_RESET);
					}
				}
				case "quit" -> {
					if(Client.state.loggedIn) {
						Client.disconnect();
					}
					Client.state.appQuit = true;
				}
				default -> System.out.printf("%sDas Komando %s Wurde nicht gefunden%s\n", ANSI_RED, command[0], ANSI_RESET);
			}
		}
	}
}
