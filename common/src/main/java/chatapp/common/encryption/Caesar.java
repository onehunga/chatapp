package chatapp.common.encryption;

import java.util.Random;

public class Caesar implements IEncryptionManager {
	private int key;

	public Caesar() {
		var rn = new Random();
		key = rn.nextInt(25) + 1;
	}

	public Caesar(int key) {
		this.key = key;
	}

	@Override
	public String encrypt(String message) {
		StringBuilder ciphertext = new StringBuilder();

		for(int i = 0; i < message.length(); i++) {
			char c = message.charAt(i);

			if(Character.isAlphabetic(c)) {
				char base = Character.isUpperCase(c) ? 'A' : 'a';

				c = (char) ((c - base + key) % 26 + base);
			}

			ciphertext.append(c);
		}

		return ciphertext.toString();
	}

	@Override
	public String decrypt(String message) {
		StringBuilder out = new StringBuilder();

		for(int i = 0; i < message.length(); i++) {
			char c = message.charAt(i);

			if(Character.isAlphabetic(c)) {
				char base = Character.isUpperCase(c) ? 'A' : 'a';

				c = (char) ((c - base + 26 - key) % 26 + base);
			}

			out.append(c);
		}

		return out.toString();
	}

	public int getKey() {
		return this.key;
	}
}
