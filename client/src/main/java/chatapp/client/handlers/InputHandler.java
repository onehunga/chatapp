package chatapp.client.handlers;

import chatapp.client.Client;
import chatapp.common.encryption.EncryptionMethod;

import java.util.Arrays;
import java.util.Scanner;
import java.util.Stack;

public class InputHandler implements Runnable {

	// Konstanten für die ausgabe von buntem Text
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
	public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
	public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
	public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
	public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
	public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
	public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
	public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

	private Scanner scanner;
	private ChatHandler chatHandler;

	public InputHandler() {
		this.scanner = new Scanner(System.in);
		chatHandler = ChatHandler.getInstance();
	}

	@Override
	public void run() {
		while(!Client.state.appQuit) {
			/*
			if(Client.state.loggedIn && Client.state.partner != null) {
				System.out.print(Client.state.partner + "> ");
			}
			 */
			var line = this.scanner.nextLine();

			var command = line.split(" ");


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
					Client.disconnect();
					Client.state.loggedIn = false;
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
						System.out.printf("%sDu chattest gerade mit %s.%s\n", ANSI_GREEN, Client.state.partner, ANSI_RESET);
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

						Client.requestChat(partner, method);
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
				default -> {
					System.out.printf("");
				}
			}
		}
	}
}
